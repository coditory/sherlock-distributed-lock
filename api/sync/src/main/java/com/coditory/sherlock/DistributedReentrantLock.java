package com.coditory.sherlock;

import com.coditory.sherlock.common.InstanceId;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.LockRequest;
import com.coditory.sherlock.common.util.Preconditions;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

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
    this.lockId = Preconditions.expectNonNull(lockId);
    this.instanceId = Preconditions.expectNonNull(instanceId);
    this.duration = Preconditions.expectNonNull(duration);
    this.driver = Preconditions.expectNonNull(driver);
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
    Preconditions.expectNonNull(duration, "Expected non null duration");
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
