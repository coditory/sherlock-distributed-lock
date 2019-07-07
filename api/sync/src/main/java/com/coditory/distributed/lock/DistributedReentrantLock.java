package com.coditory.distributed.lock;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;
import com.coditory.distributed.lock.common.LockRequest;

import java.time.Duration;

import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;

final class DistributedReentrantLock implements DistributedLock {
  private final LockId lockId;
  private final InstanceId instanceId;
  private final Duration duration;
  private final DistributedLockDriver driver;

  DistributedReentrantLock(
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
    LockRequest lockRequest = new LockRequest(lockId, instanceId, duration);
    return driver.acquireOrProlong(lockRequest);
  }

  @Override
  public boolean release() {
    return driver.release(lockId, instanceId);
  }
}
