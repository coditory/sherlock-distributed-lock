package com.coditory.sherlock.test;

import com.coditory.sherlock.DistributedLock;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.coditory.sherlock.common.util.Preconditions.expectNonEmpty;

/**
 * Use it to mock {@link DistributedLock} in tests.
 */
public final class DistributedLockMock implements DistributedLock {
  /**
   * Create a lock that can be always acquired.
   */
  public static DistributedLockMock alwaysReleasedLock(String lockId) {
    return singleStateLock(lockId, true);
  }

  /**
   * Create a lock that can be never acquired.
   */
  public static DistributedLockMock alwaysAcquiredLock(String lockId) {
    return singleStateLock(lockId, false);
  }

  /**
   * Create always released lock if released parameter is true, otherwise always acquired lock is
   * returned
   */
  public static DistributedLockMock singleStateLock(String lockId, boolean released) {
    return new DistributedLockMock(lockId, List.of(released), List.of(released));
  }

  /**
   * Create a lock returning a sequence of results for acquiring.
   */
  public static DistributedLockMock sequencedLock(
      String lockId, List<Boolean> results) {
    return new DistributedLockMock(lockId, results, results);
  }

  /**
   * Create a lock returning a sequence of results for acquiring and releasing.
   */
  public static DistributedLockMock sequencedLock(
      String lockId, List<Boolean> acquireResults, List<Boolean> releaseResults) {
    return new DistributedLockMock(lockId, acquireResults, releaseResults);
  }

  private final String lockId;
  private final ConcurrentLinkedQueue<Boolean> acquireResults;
  private final ConcurrentLinkedQueue<Boolean> releaseResults;
  private final boolean defaultAcquireResult;
  private final boolean defaultReleaseResult;
  private AtomicInteger releaseInvocations = new AtomicInteger(0);
  private AtomicInteger acquireInvocations = new AtomicInteger(0);
  private AtomicInteger releaseSuccesses = new AtomicInteger(0);
  private AtomicInteger acquireSuccesses = new AtomicInteger(0);

  private DistributedLockMock(
      String lockId, List<Boolean> acquireResults, List<Boolean> releaseResults) {
    expectNonEmpty(acquireResults, "Expected non empty acquire results");
    expectNonEmpty(releaseResults, "Expected non empty release results");
    this.lockId = expectNonEmpty(lockId, "Expected non empty lockId");
    this.acquireResults = new ConcurrentLinkedQueue<>(acquireResults);
    this.releaseResults = new ConcurrentLinkedQueue<>(releaseResults);
    this.defaultAcquireResult = acquireResults.get(acquireResults.size() - 1);
    this.defaultReleaseResult = acquireResults.get(releaseResults.size() - 1);
  }

  @Override
  public String getId() {
    return lockId;
  }

  @Override
  public boolean acquire() {
    acquireInvocations.incrementAndGet();
    boolean result = pollOrDefault(acquireResults, defaultAcquireResult);
    if (result) {
      acquireSuccesses.incrementAndGet();
    }
    return result;
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
    releaseInvocations.incrementAndGet();
    boolean result = pollOrDefault(releaseResults, defaultReleaseResult);
    if (result) {
      acquireSuccesses.incrementAndGet();
    }
    return result;
  }

  /**
   * @return true if lock was successfully acquired
   */
  public boolean wasAcquired() {
    return acquireSuccesses.get() > 0;
  }

  /**
   * @return true if lock was successfully released
   */
  public boolean wasReleased() {
    return releaseSuccesses.get() > 0;
  }

  /**
   * @return the count of successful releases
   */
  public int releaseSuccesses() {
    return releaseSuccesses.get();
  }

  /**
   * @return the count of successful acquisitions
   */
  public int aquireSuccesses() {
    return acquireSuccesses.get();
  }

  /**
   * @return true if acquire operation was invoked
   */
  public boolean wasAcquireInvoked() {
    return acquireInvocations.get() > 0;
  }

  /**
   * @return true if release operation was invoked
   */
  public boolean wasReleaseInvoked() {
    return releaseInvocations.get() > 0;
  }

  /**
   * @return the count of release invocations
   */
  public int releaseInvocations() {
    return releaseInvocations.get();
  }

  /**
   * @return the count of acquire invocations
   */
  public int acquireInvocations() {
    return acquireInvocations.get();
  }

  private boolean pollOrDefault(ConcurrentLinkedQueue<Boolean> queue, boolean defaultValue) {
    Boolean value = queue.poll();
    return value != null ? value : defaultValue;
  }
}
