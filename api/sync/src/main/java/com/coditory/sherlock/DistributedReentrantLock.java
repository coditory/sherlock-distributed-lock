package com.coditory.sherlock;

import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.LockRequest;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class DistributedReentrantLock implements DistributedLock {
  private final LockId lockId;
  private final OwnerId ownerId;
  private final Duration duration;
  private final DistributedLockDriver driver;

  DistributedReentrantLock(
      LockId lockId,
      OwnerId ownerId,
      Duration duration,
      DistributedLockDriver driver) {
    this.lockId = expectNonNull(lockId);
    this.ownerId = expectNonNull(ownerId);
    this.duration = expectNonNull(duration);
    this.driver = expectNonNull(driver);
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
    expectNonNull(duration, "Expected non null duration");
    return tryLock(duration);
  }

  @Override
  public boolean acquireForever() {
    return tryLock(null);
  }

  private boolean tryLock(Duration duration) {
    LockRequest lockRequest = new LockRequest(lockId, ownerId, duration);
    return driver.acquireOrProlong(lockRequest);
  }

  @Override
  public boolean release() {
    return driver.release(lockId, ownerId);
  }
}
