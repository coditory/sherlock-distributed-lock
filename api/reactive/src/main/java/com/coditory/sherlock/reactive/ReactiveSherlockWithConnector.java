package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.OwnerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class ReactiveSherlockWithConnector implements ReactiveSherlock {
  private final Logger logger = LoggerFactory.getLogger(ReactiveSherlockWithConnector.class);
  private final ReactiveDistributedLockConnector connector;
  private final LockDuration duration;
  private final OwnerId ownerId;

  ReactiveSherlockWithConnector(
      ReactiveDistributedLockConnector connector, OwnerId ownerId, LockDuration duration) {
    this.connector = expectNonNull(connector, "Expected non null connector");
    this.ownerId = expectNonNull(ownerId, "Expected non null ownerId");
    this.duration = expectNonNull(duration, "Expected non null duration");
  }

  @Override
  public String getOwnerId() {
    return ownerId.getValue();
  }

  @Override
  public Duration getLockDuration() {
    return duration.getValue();
  }

  @Override
  public ReactiveDistributedLock createLock(String lockId) {
    return createLock(lockId, duration);
  }

  @Override
  public ReactiveDistributedLock createLock(String lockId, Duration duration) {
    return createLock(lockId, LockDuration.of(duration));
  }

  private ReactiveDistributedLock createLock(String lockId, LockDuration duration) {
    return logCreatedLock(
        new ReactiveDistributedSingleEntrantLock(LockId.of(lockId), ownerId, duration, connector));
  }

  @Override
  public ReactiveDistributedLock createReentrantLock(String lockId) {
    return createReentrantLock(lockId, duration);
  }

  @Override
  public ReactiveDistributedLock createReentrantLock(String lockId, Duration duration) {
    return createReentrantLock(lockId, LockDuration.of(duration));
  }

  private ReactiveDistributedLock createReentrantLock(String lockId, LockDuration duration) {
    return logCreatedLock(
        new ReactiveDistributedReentrantLock(LockId.of(lockId), ownerId, duration, connector));
  }

  @Override
  public ReactiveDistributedLock createOverridingLock(String lockId) {
    return createOverridingLock(lockId, duration);
  }

  @Override
  public ReactiveDistributedLock createOverridingLock(String lockId, Duration duration) {
    return createOverridingLock(lockId, LockDuration.of(duration));
  }

  private ReactiveDistributedLock createOverridingLock(String lockId, LockDuration duration) {
    return logCreatedLock(
        new ReactiveDistributedOverridingLock(LockId.of(lockId), ownerId, duration, connector));
  }

  private ReactiveDistributedLock logCreatedLock(ReactiveDistributedLock lock) {
    logger.debug("Created lock: {}", lock);
    return lock;
  }
}
