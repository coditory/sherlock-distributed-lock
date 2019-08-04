package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.reactive.ReactiveDistributedLock;
import com.coditory.sherlock.reactive.ReactiveDistributedLockBuilder;
import com.coditory.sherlock.reactive.ReactiveSherlock;
import com.coditory.sherlock.reactive.connector.InitializationResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import com.coditory.sherlock.rxjava.RxDistributedLockBuilder.LockCreator;
import io.reactivex.Single;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;
import static com.coditory.sherlock.rxjava.PublisherToSingleConverter.convertToSingle;

final class RxSherlockWrapper implements RxSherlock {
  private final ReactiveSherlock sherlock;

  RxSherlockWrapper(ReactiveSherlock sherlock) {
    this.sherlock = expectNonNull(sherlock, "Expected non null sherlock");
  }

  @Override
  public Single<InitializationResult> initialize() {
    return convertToSingle(sherlock.initialize());
  }

  @Override
  public RxDistributedLockBuilder createLock() {
    return createLockBuilder(sherlock.createLock());
  }

  @Override
  public RxDistributedLockBuilder createReentrantLock() {
    return createLockBuilder(sherlock.createReentrantLock());
  }

  @Override
  public RxDistributedLockBuilder createOverridingLock() {
    return createLockBuilder(sherlock.createOverridingLock());
  }

  @Override
  public Single<ReleaseResult> forceReleaseAllLocks() {
    return convertToSingle(sherlock.forceReleaseAllLocks());
  }

  private RxDistributedLockBuilder createLockBuilder(
    ReactiveDistributedLockBuilder reactiveBuilder) {
    return new RxDistributedLockBuilder(createLock(reactiveBuilder))
      .withLockDuration(reactiveBuilder.getDuration())
      .withOwnerIdPolicy(reactiveBuilder.getOwnerIdPolicy());
  }

  private LockCreator createLock(ReactiveDistributedLockBuilder reactiveBuilder) {
    return (lockId, duration, ownerId) ->
      createLockAndLog(reactiveBuilder, lockId, ownerId, duration);
  }

  private RxDistributedLock createLockAndLog(
    ReactiveDistributedLockBuilder reactiveBuilder,
    LockId lockId,
    OwnerId ownerId,
    LockDuration duration) {
    ReactiveDistributedLock reactiveLock = reactiveBuilder
      .withLockDuration(duration.getValue())
      .withOwnerId(ownerId.getValue())
      .withLockId(lockId.getValue())
      .build();
    return new RxDistributedLockWrapper(reactiveLock);
  }
}
