package com.coditory.sherlock.reactive;

import com.coditory.sherlock.reactive.driver.LockResult;
import com.coditory.sherlock.reactive.driver.UnlockResult;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.coditory.sherlock.common.util.Preconditions.expectNonEmpty;

public final class ReactorDistributedLockMock implements ReactorDistributedLock {
  public static ReactorDistributedLockMock alwaysOpenedLock(String lockId) {
    return singleStateLock(lockId, true);
  }

  public static ReactorDistributedLockMock alwaysClosedLock(String lockId) {
    return singleStateLock(lockId, false);
  }

  public static ReactorDistributedLockMock singleStateLock(String lockId, boolean result) {
    return new ReactorDistributedLockMock(lockId, List.of(result), List.of(result));
  }

  public static ReactorDistributedLockMock sequencedLock(String lockId, List<Boolean> results) {
    return sequencedLock(lockId, results, results);
  }

  public static ReactorDistributedLockMock sequencedLock(
      String lockId, List<Boolean> acquireResults, List<Boolean> releaseResults) {
    return new ReactorDistributedLockMock(lockId, acquireResults, releaseResults);
  }

  private final String lockId;
  private final ConcurrentLinkedQueue<Boolean> acquireResults;
  private final ConcurrentLinkedQueue<Boolean> releaseResults;
  private final boolean defaultAcquireResult;
  private final boolean defaultReleaseResult;
  private AtomicInteger releaseInvocations = new AtomicInteger(0);
  private AtomicInteger acquireInvocations = new AtomicInteger(0);

  ReactorDistributedLockMock(
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
  public Mono<LockResult> acquire() {
    return pollOrDefault(acquireResults, defaultAcquireResult)
        .map(LockResult::of)
        .doOnNext(result -> acquireInvocations.incrementAndGet());
  }

  @Override
  public Mono<LockResult> acquire(Duration duration) {
    return acquire();
  }

  @Override
  public Mono<LockResult> acquireForever() {
    return acquire();
  }

  @Override
  public Mono<UnlockResult> release() {
    return pollOrDefault(releaseResults, defaultReleaseResult)
        .map(UnlockResult::of)
        .doOnNext(result -> releaseInvocations.incrementAndGet());
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

  private Mono<Boolean> pollOrDefault(ConcurrentLinkedQueue<Boolean> queue, boolean defaultValue) {
    return Mono.fromCallable(() -> {
      Boolean value = queue.poll();
      return value != null ? value : defaultValue;
    });
  }
}
