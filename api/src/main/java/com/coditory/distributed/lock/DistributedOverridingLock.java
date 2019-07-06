package com.coditory.distributed.lock;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;
import com.coditory.distributed.lock.common.LockRequest;

import java.time.Duration;

import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;

class DistributedOverridingLock implements DistributedLock {
  private final LockId lockId;
  private final InstanceId instanceId;
  private final Duration duration;
  private final DistributedLockDriver driver;

  DistributedOverridingLock(
      LockId lockId,
      InstanceId instanceId,
      Duration duration,
      DistributedLockDriver driver) {
    this.lockId = expectNonNull(lockId);
    this.instanceId = expectNonNull(instanceId);
    this.duration = expectNonNull(duration);
    this.driver = expectNonNull(driver);
  }

  @Override
  public String getId() {
    return lockId.getValue();
  }

  @Override
  public boolean lock() {
    return tryLock(duration);
  }

  @Override
  public boolean lock(Duration duration) {
    expectNonNull(duration, "Expected non null duration");
    return tryLock(duration);
  }

  @Override
  public boolean lockInfinitely() {
    return tryLock(null);
  }

  private boolean tryLock(Duration duration) {
    LockRequest lockRequest = new LockRequest(lockId, instanceId, duration);
    return driver.forceLock(lockRequest);
  }

  @Override
  public boolean unlock() {
    return driver.forceUnlock(lockId);
  }
}
