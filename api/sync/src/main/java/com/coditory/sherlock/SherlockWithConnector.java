package com.coditory.sherlock;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.OwnerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class SherlockWithConnector implements Sherlock {
  private final Logger logger = LoggerFactory.getLogger(SherlockWithConnector.class);
  private final DistributedLockConnector connector;
  private final LockDuration duration;
  private final OwnerId ownerId;

  SherlockWithConnector(
      DistributedLockConnector connector, OwnerId ownerId, LockDuration duration) {
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
  public DistributedLock createReentrantLock(String lockId) {
    return createReentrantLock(lockId, duration);
  }

  @Override
  public DistributedLock createReentrantLock(String lockId, Duration duration) {
    return createReentrantLock(lockId, duration);
  }

  private DistributedLock createReentrantLock(String lockId, LockDuration duration) {
    return logCreatedLock(
        new DistributedReentrantLock(LockId.of(lockId), ownerId, duration, connector));
  }

  @Override
  public DistributedLock createLock(String lockId) {
    return createLock(lockId, duration);
  }

  @Override
  public DistributedLock createLock(String lockId, Duration duration) {
    return createLock(lockId, LockDuration.of(duration));
  }

  private DistributedLock createLock(String lockId, LockDuration duration) {
    return logCreatedLock(
        new DistributedSingleEntrantLock(LockId.of(lockId), ownerId, duration, connector));
  }

  @Override
  public DistributedLock createOverridingLock(String lockId) {
    return createOverridingLock(lockId, duration);
  }

  @Override
  public DistributedLock createOverridingLock(String lockId, Duration duration) {
    return createOverridingLock(lockId, LockDuration.of(duration));
  }

  private DistributedLock createOverridingLock(String lockId, LockDuration duration) {
    return logCreatedLock(
        new DistributedOverridingLock(LockId.of(lockId), ownerId, duration, connector));
  }

  private DistributedLock logCreatedLock(DistributedLock lock) {
    logger.debug("Created lock: {}", lock);
    return lock;
  }
}
