package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.common.LockId;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class ReactiveSherlockWithDriver implements ReactiveSherlock {
  private final ReactiveDistributedLockDriver driver;
  private final Duration duration;
  private final OwnerId ownerId;

  ReactiveSherlockWithDriver(
      ReactiveDistributedLockDriver driver, OwnerId ownerId, Duration defaultDuration) {
    this.driver = expectNonNull(driver, "Expected non null driver");
    this.ownerId = expectNonNull(ownerId, "Expected non null ownerId");
    this.duration = expectNonNull(defaultDuration, "Expected non null duration");
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
  public ReactiveDistributedLock createReentrantLock(String lockId) {
    return createReentrantLock(lockId, duration);
  }

  @Override
  public ReactiveDistributedLock createReentrantLock(String lockId, Duration duration) {
    return new ReactiveDistributedReentrantLock(LockId.of(lockId), ownerId, duration, driver);
  }

  @Override
  public ReactiveDistributedLock createLock(String lockId) {
    return createLock(lockId, duration);
  }

  @Override
  public ReactiveDistributedLock createLock(String lockId, Duration duration) {
    return new ReactiveDistributedSingleEntrantLock(
        LockId.of(lockId), ownerId, duration, driver);
  }

  @Override
  public ReactiveDistributedLock createOverridingLock(String lockId) {
    return createOverridingLock(lockId, duration);
  }

  @Override
  public ReactiveDistributedLock createOverridingLock(String lockId, Duration duration) {
    return new ReactiveDistributedOverridingLock(LockId.of(lockId), ownerId, duration, driver);
  }
}
