package com.coditory.sherlock;

import com.coditory.sherlock.common.LockId;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static com.coditory.sherlock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;
import static com.coditory.sherlock.common.util.UuidGenerator.uuid;

public class DistributedLockMock implements DistributedLock {
  public static DistributedLockMock releasedInMemoryLock() {
    return releasedInMemoryLock(uuid());
  }

  public static DistributedLockMock acquiredInMemoryLock() {
    return acquiredInMemoryLock(uuid());
  }

  public static DistributedLockMock releasedInMemoryLock(String lockId) {
    return inMemoryLock(lockId, false);
  }

  public static DistributedLockMock acquiredInMemoryLock(String lockId) {
    return inMemoryLock(lockId, true);
  }

  private static DistributedLockMock inMemoryLock(String lockId, boolean state) {
    return of(InMemoryDistributedLockStub.inMemoryLock(LockId.of(lockId), state));
  }

  public static DistributedLockMock releasedReentrantInMemoryLock() {
    return releasedReentrantInMemoryLock(uuid());
  }

  public static DistributedLockMock acquiredReentrantInMemoryLock() {
    return acquiredReentrantInMemoryLock(uuid());
  }

  public static DistributedLockMock releasedReentrantInMemoryLock(String lockId) {
    return reentrantInMemoryLock(lockId, false);
  }

  public static DistributedLockMock acquiredReentrantInMemoryLock(String lockId) {
    return reentrantInMemoryLock(lockId, true);
  }

  private static DistributedLockMock reentrantInMemoryLock(String lockId, boolean state) {
    return of(InMemoryDistributedLockStub.reentrantInMemoryLock(LockId.of(lockId), state));
  }

  public static DistributedLockMock lockStub(boolean result) {
    return lockStub(uuid(), result, result);
  }

  public static DistributedLockMock lockStub(boolean acquireResult, boolean releaseResult) {
    return lockStub(uuid(), acquireResult, releaseResult);
  }

  public static DistributedLockMock lockStub(String lockId, boolean result) {
    return of(lockStub(lockId, result, result));
  }

  public static DistributedLockMock lockStub(
    String lockId, boolean acquireResult, boolean releaseResult) {
    SequencedDistributedLockStub lock = new SequencedDistributedLockStub(
      lockId, List.of(acquireResult), List.of(releaseResult));
    return of(lock);
  }

  public static DistributedLockMock sequencedLock(
    List<Boolean> acquireResults, List<Boolean> releaseResults) {
    return sequencedLock(uuid(), acquireResults, releaseResults);
  }

  public static DistributedLockMock sequencedLock(
    String lockId, List<Boolean> acquireResults, List<Boolean> releaseResults) {
    return of(new SequencedDistributedLockStub(lockId, acquireResults, releaseResults));
  }

  private static DistributedLockMock of(DistributedLock lock) {
    return new DistributedLockMock(lock);
  }

  private final DistributedLock lock;
  private final AtomicInteger releases = new AtomicInteger(0);
  private final AtomicInteger acquisitions = new AtomicInteger(0);
  private final AtomicInteger successfulReleases = new AtomicInteger(0);
  private final AtomicInteger successfulAcquisitions = new AtomicInteger(0);

  private DistributedLockMock(DistributedLock lock) {
    this.lock = lock;
  }

  @Override
  public String getId() {
    return lock.getId();
  }

  @Override
  public boolean acquire() {
    return acquire(lock::acquire);
  }

  @Override
  public boolean acquire(Duration duration) {
    return acquire(() -> lock.acquire(duration));
  }

  @Override
  public boolean acquireForever() {
    return acquire(lock::acquireForever);
  }

  private boolean acquire(Supplier<Boolean> acquireAction) {
    acquisitions.incrementAndGet();
    boolean result = acquireAction.get();
    if (result) {
      successfulAcquisitions.incrementAndGet();
    }
    return result;
  }

  @Override
  public boolean release() {
    releases.incrementAndGet();
    boolean result = lock.release();
    if (result) {
      successfulReleases.incrementAndGet();
    }
    return result;
  }

  /**
   * @return the count of successful releases
   */
  public int successfulReleases() {
    return successfulReleases.get();
  }

  /**
   * @return the count of successful acquisitions
   */
  public int successfulAcquisitions() {
    return successfulAcquisitions.get();
  }

  /**
   * @return the count of all releases (successful and unsuccessful)
   */
  public int releases() {
    return releases.get();
  }

  /**
   * @return the count of all acquisitions (successful and unsuccessful)
   */
  public int acquisitions() {
    return acquisitions.get();
  }

  /**
   * @return the count of rejected releases
   */
  public int rejectedReleases() {
    return releases() - successfulReleases();
  }

  /**
   * @return the count of rejected acquisitions
   */
  public int rejectedAcquisitions() {
    return acquisitions() - successfulAcquisitions();
  }

  /**
   * @return true if lock was successfully acquired at least once
   */
  public boolean wasAcquired() {
    return successfulAcquisitions() > 0;
  }

  /**
   * @return true if lock was successfully released at least once
   */
  public boolean wasReleased() {
    return successfulReleases() > 0;
  }

  /**
   * @return true if lock was successfully acquired and released
   */
  public boolean wasAcquiredAndReleased() {
    return wasAcquired() && wasReleased();
  }

  /**
   * @return true if lock was acquired without success at least once
   */
  public boolean wasAcquireRejected() {
    return successfulAcquisitions() < acquisitions();
  }

  /**
   * @return true if lock was released without success at least once
   */
  public boolean wasReleaseRejected() {
    return successfulReleases() < releases();
  }

  /**
   * @return true if acquire operation was invoked at least once
   */
  public boolean wasAcquireInvoked() {
    return acquisitions() > 0;
  }

  /**
   * @return true if release operation was invoked at least once
   */
  public boolean wasReleaseInvoked() {
    return releases() > 0;
  }

  private static class InMemoryDistributedLockStub implements DistributedLock {
    private final LockId lockId;
    private final boolean reentrant;
    private AtomicBoolean acquired;

    static InMemoryDistributedLockStub reentrantInMemoryLock(LockId lockId, boolean acquired) {
      return new InMemoryDistributedLockStub(lockId, true, acquired);
    }

    static InMemoryDistributedLockStub inMemoryLock(LockId lockId, boolean acquired) {
      return new InMemoryDistributedLockStub(lockId, false, acquired);
    }

    private InMemoryDistributedLockStub(LockId lockId, boolean reentrant, boolean acquired) {
      this.lockId = expectNonNull(lockId, "Expected non null lockId");
      this.reentrant = reentrant;
      this.acquired = new AtomicBoolean(acquired);
    }

    @Override
    public String getId() {
      return lockId.getValue();
    }

    @Override
    public boolean acquire() {
      if (reentrant) {
        acquired.set(true);
        return true;
      } else {
        return acquired.compareAndSet(false, true);
      }
    }

    @Override
    public boolean acquire(Duration duration) {
      return acquire();
    }

    @Override
    public boolean acquireForever() {
      return acquire();
    }

    @Override
    public boolean release() {
      return acquired.compareAndSet(true, false);
    }
  }

  static class SequencedDistributedLockStub implements DistributedLock {
    private final LockId lockId;
    private final ConcurrentLinkedQueue<Boolean> acquireResults;
    private final ConcurrentLinkedQueue<Boolean> releaseResults;
    private final boolean defaultAcquireResult;
    private final boolean defaultReleaseResult;

    private SequencedDistributedLockStub(
      String lockId, List<Boolean> acquireResults, List<Boolean> releaseResults) {
      this(LockId.of(lockId), acquireResults, releaseResults);
    }

    private SequencedDistributedLockStub(
      LockId lockId, List<Boolean> acquireResults, List<Boolean> releaseResults) {
      expectNonEmpty(acquireResults, "Expected non empty acquire results");
      expectNonEmpty(releaseResults, "Expected non empty release results");
      this.lockId = expectNonNull(lockId, "Expected non null lockId");
      this.acquireResults = new ConcurrentLinkedQueue<>(acquireResults);
      this.releaseResults = new ConcurrentLinkedQueue<>(releaseResults);
      this.defaultAcquireResult = acquireResults.get(acquireResults.size() - 1);
      this.defaultReleaseResult = releaseResults.get(releaseResults.size() - 1);
    }

    @Override
    public String getId() {
      return lockId.getValue();
    }

    @Override
    public boolean acquire() {
      return pollOrDefault(acquireResults, defaultAcquireResult);
    }

    @Override
    public boolean acquire(Duration duration) {
      return acquire();
    }

    @Override
    public boolean acquireForever() {
      return acquire();
    }

    @Override
    public boolean release() {
      return pollOrDefault(releaseResults, defaultReleaseResult);
    }

    private boolean pollOrDefault(ConcurrentLinkedQueue<Boolean> queue, boolean defaultValue) {
      Boolean value = queue.poll();
      return value != null ? value : defaultValue;
    }
  }
}
