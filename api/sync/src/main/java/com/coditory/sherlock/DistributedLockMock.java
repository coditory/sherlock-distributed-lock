package com.coditory.sherlock;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.coditory.sherlock.common.util.Preconditions.expectNonEmpty;

public final class DistributedLockMock implements DistributedLock {
  public static DistributedLockMock alwaysOpenedLock(String lockId) {
    return singleStateLock(lockId, true);
  }

  public static DistributedLockMock alwaysClosedLock(String lockId) {
    return singleStateLock(lockId, false);
  }

  public static DistributedLockMock singleStateLock(String lockId, boolean result) {
    return new DistributedLockMock(lockId, List.of(result), List.of(result));
  }

  public static DistributedLockMock sequencedLock(
      String lockId, List<Boolean> results) {
    return new DistributedLockMock(lockId, results, results);
  }

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

  DistributedLockMock(
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
    releaseInvocations.incrementAndGet();
    return pollOrDefault(releaseResults, defaultReleaseResult);
  }

  public boolean wasAcquired() {
    return acquireInvocations() > 0;
  }

  public boolean wasReleased() {
    return releaseInvocations() > 0;
  }

  public int releaseInvocations() {
    return releaseInvocations.get();
  }

  public int acquireInvocations() {
    return acquireInvocations.get();
  }

  private boolean pollOrDefault(ConcurrentLinkedQueue<Boolean> queue, boolean defaultValue) {
    Boolean value = queue.poll();
    return value != null ? value : defaultValue;
  }
}
