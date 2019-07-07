package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.InstanceId;
import com.coditory.sherlock.common.LockId;

import java.time.Duration;
import java.util.function.Function;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

public final class ReactiveSherlock {
  private final ReactiveDistributedLockDriver driver;
  private final Duration duration;
  private final InstanceId instanceId;

  ReactiveSherlock(
      ReactiveDistributedLockDriver driver, InstanceId instanceId, Duration defaultDuration) {
    this.driver = expectNonNull(driver, "Expected non null driver");
    this.instanceId = expectNonNull(instanceId, "Expected non null instanceId");
    this.duration = expectNonNull(defaultDuration, "Expected non null duration");
  }

  public InstanceId getInstanceId() {
    return instanceId;
  }

  public Duration getDuration() {
    return duration;
  }

  public ReactiveDistributedLock createReentrantLock(String lockId) {
    return createReentrantLock(lockId, duration);
  }

  public ReactiveDistributedLock createReentrantLock(String lockId, Duration duration) {
    return new ReactiveDistributedReentrantLock(LockId.of(lockId), instanceId, duration, driver);
  }

  public ReactiveDistributedLock createLock(String lockId) {
    return createLock(lockId, duration);
  }

  public ReactiveDistributedLock createLock(String lockId, Duration duration) {
    return new ReactiveDistributedSingleEntrantLock(
        LockId.of(lockId), instanceId, duration, driver);
  }

  public ReactiveDistributedLock createOverridingLock(String lockId) {
    return createOverridingLock(lockId, duration);
  }

  public ReactiveDistributedLock createOverridingLock(String lockId, Duration duration) {
    return new ReactiveDistributedOverridingLock(LockId.of(lockId), instanceId, duration, driver);
  }

  public <T> T map(Function<ReactiveSherlock, T> mapper) {
    return mapper.apply(this);
  }
}
