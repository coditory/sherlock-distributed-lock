package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.InstanceId;
import com.coditory.sherlock.common.LockId;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class ReactiveSherlockWithDriver implements ReactiveSherlock {
  private final ReactiveDistributedLockDriver driver;
  private final Duration duration;
  private final InstanceId instanceId;

  ReactiveSherlockWithDriver(
      ReactiveDistributedLockDriver driver, InstanceId instanceId, Duration defaultDuration) {
    this.driver = expectNonNull(driver, "Expected non null driver");
    this.instanceId = expectNonNull(instanceId, "Expected non null instanceId");
    this.duration = expectNonNull(defaultDuration, "Expected non null duration");
  }

  @Override
  public String getOwnerId() {
    return instanceId.getValue();
  }

  @Override
  public Duration getLockDuration() {
    return duration;
  }

  @Override
  public ReactiveDistributedLock createReentrantLock(String lockId) {
    return createReentrantLock(lockId, duration);
  }

  @Override
  public ReactiveDistributedLock createReentrantLock(String lockId, Duration duration) {
    return new ReactiveDistributedReentrantLock(LockId.of(lockId), instanceId, duration, driver);
  }

  @Override
  public ReactiveDistributedLock createLock(String lockId) {
    return createLock(lockId, duration);
  }

  @Override
  public ReactiveDistributedLock createLock(String lockId, Duration duration) {
    return new ReactiveDistributedSingleEntrantLock(
        LockId.of(lockId), instanceId, duration, driver);
  }

  @Override
  public ReactiveDistributedLock createOverridingLock(String lockId) {
    return createOverridingLock(lockId, duration);
  }

  @Override
  public ReactiveDistributedLock createOverridingLock(String lockId, Duration duration) {
    return new ReactiveDistributedOverridingLock(LockId.of(lockId), instanceId, duration, driver);
  }
}
