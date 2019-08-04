package com.coditory.sherlock.reactor;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.reactive.ReactiveDistributedLock;
import com.coditory.sherlock.reactive.ReactiveDistributedLockBuilder;
import com.coditory.sherlock.reactive.ReactiveSherlock;
import com.coditory.sherlock.reactive.connector.InitializationResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import com.coditory.sherlock.reactor.ReactorDistributedLockBuilder.LockCreator;
import reactor.core.publisher.Mono;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;
import static com.coditory.sherlock.reactor.PublisherToMonoConverter.convertToMono;

final class ReactorSherlockWrapper implements ReactorSherlock {
  private final ReactiveSherlock sherlock;

  ReactorSherlockWrapper(ReactiveSherlock sherlock) {
    this.sherlock = expectNonNull(sherlock, "Expected non null sherlock");
  }

  @Override
  public Mono<InitializationResult> initialize() {
    return convertToMono(sherlock.initialize());
  }

  @Override
  public ReactorDistributedLockBuilder createLock() {
    return createLockBuilder(sherlock.createLock());
  }

  @Override
  public ReactorDistributedLockBuilder createReentrantLock() {
    return createLockBuilder(sherlock.createReentrantLock());
  }

  @Override
  public ReactorDistributedLockBuilder createOverridingLock() {
    return createLockBuilder(sherlock.createOverridingLock());
  }

  @Override
  public Mono<ReleaseResult> forceReleaseAllLocks() {
    return convertToMono(sherlock.forceReleaseAllLocks());
  }

  private ReactorDistributedLockBuilder createLockBuilder(
    ReactiveDistributedLockBuilder reactiveBuilder) {
    return new ReactorDistributedLockBuilder(createLock(reactiveBuilder))
      .withLockDuration(reactiveBuilder.getDuration())
      .withOwnerIdPolicy(reactiveBuilder.getOwnerIdPolicy());
  }

  private LockCreator createLock(ReactiveDistributedLockBuilder reactiveBuilder) {
    return (lockId, duration, ownerId) ->
      createLockAndLog(reactiveBuilder, lockId, ownerId, duration);
  }

  private ReactorDistributedLock createLockAndLog(
    ReactiveDistributedLockBuilder reactiveBuilder,
    LockId lockId,
    OwnerId ownerId,
    LockDuration duration) {
    ReactiveDistributedLock reactiveLock = reactiveBuilder
      .withLockDuration(duration.getValue())
      .withOwnerId(ownerId.getValue())
      .withLockId(lockId.getValue())
      .build();
    return new ReactorDistributedLockWrapper(reactiveLock);
  }
}
