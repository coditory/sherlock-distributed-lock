package com.coditory.sherlock;

import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.common.LockId;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class SherlockWithDriver implements Sherlock {
  private final DistributedLockDriver driver;
  private final Duration duration;
  private final OwnerId ownerId;

  SherlockWithDriver(
      DistributedLockDriver driver, OwnerId ownerId, Duration duration) {
    this.driver = expectNonNull(driver, "Expected non null driver");
    this.ownerId = expectNonNull(ownerId, "Expected non null ownerId");
    this.duration = expectNonNull(duration, "Expected non null duration");
  }

  @Override
  public String getOwnerId() {
    return ownerId.getValue();
  }

  @Override
  public Duration getLockDuration() {
    return duration;
  }

  @Override
  public DistributedLock createReentrantLock(String lockId) {
    return createReentrantLock(lockId, duration);
  }

  @Override
  public DistributedLock createReentrantLock(String lockId, Duration duration) {
    return new DistributedReentrantLock(LockId.of(lockId), ownerId, duration, driver);
  }

  @Override
  public DistributedLock createLock(String lockId) {
    return createLock(lockId, duration);
  }

  @Override
  public DistributedLock createLock(String lockId, Duration duration) {
    return new DistributedSingleEntrantLock(LockId.of(lockId), ownerId, duration, driver);
  }

  @Override
  public DistributedLock createOverridingLock(String lockId) {
    return createOverridingLock(lockId, duration);
  }

  @Override
  public DistributedLock createOverridingLock(String lockId, Duration duration) {
    return new DistributedOverridingLock(LockId.of(lockId), ownerId, duration, driver);
  }
}
