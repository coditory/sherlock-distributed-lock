package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.coditory.sherlock.util.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.util.Preconditions.expectNonNull;
import static com.coditory.sherlock.util.UuidGenerator.uuid;

public final class ReactorDistributedLockMock implements ReactorDistributedLock {
  public static ReactorDistributedLockMock releasedInMemoryLock() {
    return releasedInMemoryLock(uuid());
  }

  public static ReactorDistributedLockMock acquiredInMemoryLock() {
    return acquiredInMemoryLock(uuid());
  }

  public static ReactorDistributedLockMock releasedInMemoryLock(String lockId) {
    return inMemoryLock(lockId, false);
  }

  public static ReactorDistributedLockMock acquiredInMemoryLock(String lockId) {
    return inMemoryLock(lockId, true);
  }

  private static ReactorDistributedLockMock inMemoryLock(String lockId, boolean state) {
    return of(InMemoryDistributedLockStub.inMemoryLock(LockId.of(lockId), state));
  }

  public static ReactorDistributedLockMock releasedReentrantInMemoryLock() {
    return releasedReentrantInMemoryLock(uuid());
  }

  public static ReactorDistributedLockMock acquiredReentrantInMemoryLock() {
    return acquiredReentrantInMemoryLock(uuid());
  }

  public static ReactorDistributedLockMock releasedReentrantInMemoryLock(String lockId) {
    return reentrantInMemoryLock(lockId, false);
  }

  public static ReactorDistributedLockMock acquiredReentrantInMemoryLock(String lockId) {
    return reentrantInMemoryLock(lockId, true);
  }

  private static ReactorDistributedLockMock reentrantInMemoryLock(String lockId, boolean state) {
    return of(InMemoryDistributedLockStub.reentrantInMemoryLock(LockId.of(lockId), state));
  }

  public static ReactorDistributedLockMock lockStub(boolean result) {
    return lockStub(uuid(), result);
  }

  public static ReactorDistributedLockMock lockStub(boolean acquireResult, boolean releaseResult) {
    return lockStub(uuid(), acquireResult, releaseResult);
  }

  public static ReactorDistributedLockMock lockStub(String lockId, boolean result) {
    return of(lockStub(lockId, result, result));
  }

  public static ReactorDistributedLockMock lockStub(
    String lockId, boolean acquireResult, boolean releaseResult) {
    SequencedDistributedLockStub lock = new SequencedDistributedLockStub(
      lockId, List.of(acquireResult), List.of(releaseResult));
    return of(lock);
  }

  public static ReactorDistributedLockMock sequencedLock(
    List<Boolean> acquireResults, List<Boolean> releaseResults) {
    return of(sequencedLock(uuid(), acquireResults, releaseResults));
  }

  public static ReactorDistributedLockMock sequencedLock(
    String lockId, List<Boolean> acquireResults, List<Boolean> releaseResults) {
    return of(new SequencedDistributedLockStub(lockId, acquireResults, releaseResults));
  }

  private static ReactorDistributedLockMock of(ReactorDistributedLock lock) {
    return new ReactorDistributedLockMock(lock);
  }

  private final ReactorDistributedLock lock;
  private final AtomicInteger releases = new AtomicInteger(0);
  private final AtomicInteger acquisitions = new AtomicInteger(0);
  private final AtomicInteger successfulReleases = new AtomicInteger(0);
  private final AtomicInteger successfulAcquisitions = new AtomicInteger(0);

  private ReactorDistributedLockMock(ReactorDistributedLock lock) {
    this.lock = lock;
  }

  @Override
  public String getId() {
    return lock.getId();
  }

  @Override
  public Mono<AcquireResult> acquire() {
    return lock.acquire()
      .map(this::incrementAcquireCounters);
  }

  @Override
  public Mono<AcquireResult> acquire(Duration duration) {
    return acquire();
  }

  @Override
  public Mono<AcquireResult> acquireForever() {
    return acquire();
  }

  private AcquireResult incrementAcquireCounters(AcquireResult result) {
    acquisitions.incrementAndGet();
    if (result.isAcquired()) {
      successfulAcquisitions.incrementAndGet();
    }
    return result;
  }

  @Override
  public Mono<ReleaseResult> release() {
    return lock.release()
      .map(this::incrementReleaseCounters);
  }

  private ReleaseResult incrementReleaseCounters(ReleaseResult result) {
    releases.incrementAndGet();
    if (result.isReleased()) {
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

  private static class InMemoryDistributedLockStub implements ReactorDistributedLock {
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
    public Mono<AcquireResult> acquire() {
      return Mono.fromCallable(this::acquireSync)
        .map(AcquireResult::of);
    }

    private boolean acquireSync() {
      if (reentrant) {
        acquired.set(true);
        return true;
      } else {
        return acquired.compareAndSet(false, true);
      }
    }

    @Override
    public Mono<AcquireResult> acquire(Duration duration) {
      return acquire();
    }

    @Override
    public Mono<AcquireResult> acquireForever() {
      return acquire();
    }

    @Override
    public Mono<ReleaseResult> release() {
      return Mono.fromCallable(this::releaseSync)
        .map(ReleaseResult::of);
    }

    private boolean releaseSync() {
      return acquired.compareAndSet(true, false);
    }
  }

  static class SequencedDistributedLockStub implements ReactorDistributedLock {
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
    public Mono<AcquireResult> acquire() {
      return pollOrDefault(acquireResults, defaultAcquireResult)
        .map(AcquireResult::of);
    }

    @Override
    public Mono<AcquireResult> acquire(Duration duration) {
      return acquire();
    }

    @Override
    public Mono<AcquireResult> acquireForever() {
      return acquire();
    }

    @Override
    public Mono<ReleaseResult> release() {
      return pollOrDefault(releaseResults, defaultReleaseResult)
        .map(ReleaseResult::of);
    }

    private Mono<Boolean> pollOrDefault(
      ConcurrentLinkedQueue<Boolean> queue, boolean defaultValue) {
      return Mono.fromCallable(() -> {
        Boolean value = queue.poll();
        return value != null ? value : defaultValue;
      });
    }
  }
}
