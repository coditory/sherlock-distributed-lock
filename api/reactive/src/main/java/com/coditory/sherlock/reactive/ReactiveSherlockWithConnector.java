package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.common.OwnerIdPolicy;
import com.coditory.sherlock.reactive.ReactiveDelegatingDistributedLock.AcquireAction;
import com.coditory.sherlock.reactive.ReactiveDelegatingDistributedLock.ReleaseAction;
import com.coditory.sherlock.reactive.ReactiveDistributedLockBuilder.LockCreator;
import com.coditory.sherlock.reactive.connector.InitializationResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Flow.Publisher;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class ReactiveSherlockWithConnector implements ReactiveSherlock {
  private final Logger logger = LoggerFactory.getLogger(ReactiveSherlockWithConnector.class);
  private final ReactiveDistributedLockConnector connector;
  private final LockDuration defaultDuration;
  private final OwnerIdPolicy defaultOwnerIdPolicy;

  ReactiveSherlockWithConnector(
    ReactiveDistributedLockConnector connector,
    OwnerIdPolicy defaultOwnerIdPolicy,
    LockDuration defaultDuration) {
    this.connector = expectNonNull(connector, "Expected non null connector");
    this.defaultOwnerIdPolicy =
      expectNonNull(defaultOwnerIdPolicy, "Expected non null defaultOwnerIdPolicy");
    this.defaultDuration =
      expectNonNull(defaultDuration, "Expected non null defaultDuration");
  }

  @Override
  public Publisher<InitializationResult> initialize() {
    logger.debug("Initializing sherlock infrastructure");
    return connector.initialize();
  }

  @Override
  public ReactiveDistributedLockBuilder createLock() {
    return createLockBuilder(connector::acquire, connector::release);
  }

  @Override
  public ReactiveDistributedLockBuilder createReentrantLock() {
    return createLockBuilder(connector::acquireOrProlong, connector::release);
  }

  @Override
  public ReactiveDistributedLockBuilder createOverridingLock() {
    return createLockBuilder(connector::forceAcquire, (id, __) -> connector.forceRelease(id));
  }

  @Override
  public Publisher<ReleaseResult> forceReleaseAllLocks() {
    return connector.forceReleaseAll();
  }

  private ReactiveDistributedLockBuilder createLockBuilder(
    AcquireAction acquireAction,
    ReleaseAction releaseAction) {
    return new ReactiveDistributedLockBuilder(createLock(acquireAction, releaseAction))
      .withLockDuration(defaultDuration)
      .withOwnerIdPolicy(defaultOwnerIdPolicy);
  }

  private LockCreator createLock(
    AcquireAction acquireAction,
    ReleaseAction releaseAction) {
    return (lockId, duration, ownerId) ->
      createLockAndLog(acquireAction, releaseAction, lockId, ownerId, duration);
  }

  private ReactiveDistributedLock createLockAndLog(
    AcquireAction acquireAction,
    ReleaseAction releaseAction,
    LockId lockId,
    OwnerId ownerId,
    LockDuration duration) {
    ReactiveDistributedLock lock = new ReactiveDelegatingDistributedLock(
      acquireAction, releaseAction, lockId, ownerId, duration);
    logger.debug("Created lock: {}", lock);
    return lock;
  }
}
