package com.coditory.xlock.api;

import com.coditory.xlock.common.InstanceId;
import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.driver.DistributedLockDriver;

import java.time.Duration;

import static com.coditory.xlock.common.util.Preconditions.expectNonNull;

public final class DistributedLocks {
  private final DistributedLockDriver driver;
  private final Duration defaultDuration;
  private final InstanceId instanceId;

  DistributedLocks(
      DistributedLockDriver driver, InstanceId instanceId, Duration defaultDuration) {
    this.driver = expectNonNull(driver, "Expected non null driver");
    this.instanceId = expectNonNull(instanceId, "Expected non null instanceId");
    this.defaultDuration = expectNonNull(defaultDuration, "Expected non null defaultDuration");
  }

  public InstanceId getInstanceId() {
    return instanceId;
  }

  public Duration getDefaultDuration() {
    return defaultDuration;
  }

  public DistributedLock createReentrantLock(String lockId) {
    return createReentrantLock(lockId, defaultDuration);
  }

  public DistributedLock createReentrantLock(String lockId, Duration duration) {
    return new DistributedReentrantLock(LockId.of(lockId), instanceId, duration, driver);
  }

  public DistributedLock createLock(String lockId) {
    return createLock(lockId, defaultDuration);
  }

  public DistributedLock createLock(String lockId, Duration duration) {
    return new DistributedSingleEntrantLock(LockId.of(lockId), instanceId, duration, driver);
  }

  public DistributedLock createOverridingLock(String lockId) {
    return createOverridingLock(lockId, defaultDuration);
  }

  public DistributedLock createOverridingLock(String lockId, Duration duration) {
    return new DistributedOverridingLock(LockId.of(lockId), instanceId, duration, driver);
  }
}
