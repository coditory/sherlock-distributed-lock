package com.coditory.sherlock;

import com.coditory.sherlock.common.InstanceId;
import com.coditory.sherlock.common.LockId;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

public final class Sherlock {
  private final DistributedLockDriver driver;
  private final Duration duration;
  private final InstanceId instanceId;

  Sherlock(
      DistributedLockDriver driver, InstanceId instanceId, Duration duration) {
    this.driver = expectNonNull(driver, "Expected non null driver");
    this.instanceId = expectNonNull(instanceId, "Expected non null instanceId");
    this.duration = expectNonNull(duration, "Expected non null duration");
  }

  public InstanceId getInstanceId() {
    return instanceId;
  }

  public Duration getLockDuration() {
    return duration;
  }

  public DistributedLock createReentrantLock(String lockId) {
    return createReentrantLock(lockId, duration);
  }

  public DistributedLock createReentrantLock(String lockId, Duration duration) {
    return new DistributedReentrantLock(LockId.of(lockId), instanceId, duration, driver);
  }

  public DistributedLock createLock(String lockId) {
    return createLock(lockId, duration);
  }

  public DistributedLock createLock(String lockId, Duration duration) {
    return new DistributedSingleEntrantLock(LockId.of(lockId), instanceId, duration, driver);
  }

  public DistributedLock createOverridingLock(String lockId) {
    return createOverridingLock(lockId, duration);
  }

  public DistributedLock createOverridingLock(String lockId, Duration duration) {
    return new DistributedOverridingLock(LockId.of(lockId), instanceId, duration, driver);
  }
}
