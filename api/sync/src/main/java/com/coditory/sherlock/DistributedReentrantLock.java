package com.coditory.sherlock;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.LockRequest;
import com.coditory.sherlock.common.OwnerId;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class DistributedReentrantLock implements DistributedLock {
  private final LockResultLogger logger;
  private final LockId lockId;
  private final OwnerId ownerId;
  private final LockDuration duration;
  private final DistributedLockConnector connector;

  DistributedReentrantLock(
      LockId lockId,
      OwnerId ownerId,
      LockDuration duration,
      DistributedLockConnector connector) {
    this.lockId = expectNonNull(lockId);
    this.ownerId = expectNonNull(ownerId);
    this.duration = expectNonNull(duration);
    this.connector = expectNonNull(connector);
    this.logger = new LockResultLogger(lockId, this.getClass());
  }

  @Override
  public String getId() {
    return lockId.getValue();
  }

  @Override
  public boolean acquire() {
    return tryLock(duration);
  }

  @Override
  public boolean acquire(Duration duration) {
    return tryLock(LockDuration.of(duration));
  }

  @Override
  public boolean acquireForever() {
    return tryLock(null);
  }

  private boolean tryLock(LockDuration duration) {
    LockRequest lockRequest = new LockRequest(lockId, ownerId, duration);
    return logger.logAcquireResult(
        connector.acquireOrProlong(lockRequest));
  }

  @Override
  public boolean release() {
    return logger.logReleaseResult(
        connector.release(lockId, ownerId));
  }
}
