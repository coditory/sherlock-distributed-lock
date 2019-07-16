package com.coditory.sherlock.rxjava.test;

import com.coditory.sherlock.reactive.connector.AcquireResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import com.coditory.sherlock.rxjava.RxJavaDistributedLock;
import io.reactivex.Single;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.coditory.sherlock.common.util.Preconditions.expectNonEmpty;

/**
 * Use it to mock {@link RxJavaDistributedLock} in tests.
 */
public final class RxJavaDistributedLockMock implements RxJavaDistributedLock {
  /**
   * Create a lock that can be always acquired.
   *
   * @param lockId the lock id
   * @return created mock
   */
  public static RxJavaDistributedLockMock alwaysReleasedLock(String lockId) {
    return singleStateLock(lockId, true);
  }

  /**
   * Create a lock that can be never acquired.
   *
   * @param lockId the lock id
   * @return created mock
   */
  public static RxJavaDistributedLockMock alwaysAcquiredLock(String lockId) {
    return singleStateLock(lockId, false);
  }

  /**
   * Create always released lock if released parameter is true, otherwise always acquired lock is
   * returned
   *
   * @param lockId the lock id
   * @param released the lock state
   * @return created mock
   */
  public static RxJavaDistributedLockMock singleStateLock(String lockId, boolean released) {
    return new RxJavaDistributedLockMock(lockId, List.of(released), List.of(released));
  }

  /**
   * Create a lock returning a sequence of results for acquiring.
   *
   * @param lockId the lock id
   * @param results the sequence of acquire and release results
   * @return created mock
   */
  public static RxJavaDistributedLockMock sequencedLock(String lockId, List<Boolean> results) {
    return sequencedLock(lockId, results, results);
  }

  /**
   * Create a lock returning a sequence of results for acquiring and releasing.
   *
   * @param lockId the lock id
   * @param acquireResults the sequence of acquire results
   * @param releaseResults the sequence of release results
   * @return created mock of distributed lock
   */
  public static RxJavaDistributedLockMock sequencedLock(
      String lockId, List<Boolean> acquireResults, List<Boolean> releaseResults) {
    return new RxJavaDistributedLockMock(lockId, acquireResults, releaseResults);
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

  private RxJavaDistributedLockMock(
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
  public Single<AcquireResult> acquire() {
    return pollOrDefault(acquireResults, defaultAcquireResult)
        .doOnSuccess(this::incrementAcquireCounters)
        .map(AcquireResult::of);
  }

  private void incrementAcquireCounters(boolean acquired) {
    acquireInvocations.incrementAndGet();
    if (acquired) {
      acquireSuccesses.incrementAndGet();
    }
  }

  @Override
  public Single<AcquireResult> acquire(Duration duration) {
    return acquire();
  }

  @Override
  public Single<AcquireResult> acquireForever() {
    return acquire();
  }

  @Override
  public Single<ReleaseResult> release() {
    return pollOrDefault(releaseResults, defaultReleaseResult)
        .doOnSuccess(this::incrementReleaseCounters)
        .map(ReleaseResult::of);
  }

  private void incrementReleaseCounters(boolean released) {
    releaseInvocations.incrementAndGet();
    if (released) {
      releaseSuccesses.incrementAndGet();
    }
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
   * @return true if lock was at least once acquired without success
   */
  public boolean wasAcquireRejected() {
    return acquireSuccesses.get() < acquireInvocations.get();
  }

  /**
   * @return true if lock was at least once acquired without success
   */
  public boolean wasReleaseRejected() {
    return releaseSuccesses.get() < releaseInvocations.get();
  }

  /**
   * @return true if lock was successfully acquired and released
   */
  public boolean wasAcquiredAndReleased() {
    return wasAcquired() && wasReleased();
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

  private Single<Boolean> pollOrDefault(
      ConcurrentLinkedQueue<Boolean> queue, boolean defaultValue) {
    return Single.fromCallable(() -> {
      Boolean value = queue.poll();
      return value != null ? value : defaultValue;
    });
  }
}
