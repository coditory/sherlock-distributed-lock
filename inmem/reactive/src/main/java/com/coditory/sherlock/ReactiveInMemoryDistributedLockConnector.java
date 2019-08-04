package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.Flow.Publisher;

import static reactor.adapter.JdkFlowAdapter.publisherToFlowPublisher;

class ReactiveInMemoryDistributedLockConnector implements ReactiveDistributedLockConnector {
  private final InMemoryDistributedLockStorage storage;
  private final Clock clock;

  ReactiveInMemoryDistributedLockConnector(Clock clock, InMemoryDistributedLockStorage storage) {
    this.clock = clock;
    this.storage = storage;
  }

  @Override
  public Publisher<InitializationResult> initialize() {
    return publisherToFlowPublisher(Mono.just(InitializationResult.of(true)));
  }

  @Override
  public Publisher<AcquireResult> acquire(LockRequest lockRequest) {
    return publisherToFlowPublisher(
      Mono.fromCallable(() -> storage.acquire(lockRequest, now()))
        .map(AcquireResult::of));
  }

  @Override
  public Publisher<AcquireResult> acquireOrProlong(LockRequest lockRequest) {
    return publisherToFlowPublisher(
      Mono.just(storage.acquireOrProlong(lockRequest, now()))
        .map(AcquireResult::of));
  }

  @Override
  public Publisher<AcquireResult> forceAcquire(LockRequest lockRequest) {
    return publisherToFlowPublisher(
      Mono.fromCallable(() -> storage.forceAcquire(lockRequest, now()))
        .map(AcquireResult::of));
  }

  @Override
  public Publisher<ReleaseResult> release(LockId lockId, OwnerId ownerId) {
    return publisherToFlowPublisher(
      Mono.fromCallable(() -> storage.release(lockId, now(), ownerId))
        .map(ReleaseResult::of));
  }

  @Override
  public Publisher<ReleaseResult> forceRelease(LockId lockId) {
    return publisherToFlowPublisher(
      Mono.fromCallable(() -> storage.forceRelease(lockId, now()))
        .map(ReleaseResult::of));
  }

  @Override
  public Publisher<ReleaseResult> forceReleaseAll() {
    return publisherToFlowPublisher(
      Mono.fromCallable(() -> storage.forceReleaseAll(now()))
        .map(ReleaseResult::of));
  }

  private Instant now() {
    return clock.instant();
  }
}
