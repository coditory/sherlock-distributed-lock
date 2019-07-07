package com.coditory.distributed.lock.reactive;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;

import java.time.Duration;

import static com.coditory.distributed.lock.common.InstanceId.uniqueInstanceId;
import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;

public final class ReactiveDistributedLocks {
  private final ReactiveDistributedLockDriver driver;
  private final Duration defaultDuration;
  private final InstanceId instanceId;

  ReactiveDistributedLocks(
      ReactiveDistributedLockDriver driver, InstanceId instanceId, Duration defaultDuration) {
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

  public ReactiveDistributedLock createReentrantLock(String lockId) {
    return createReentrantLock(lockId, defaultDuration);
  }

  public ReactiveDistributedLock createReentrantLock(String lockId, Duration duration) {
    return new ReactiveDistributedReentrantLock(LockId.of(lockId), instanceId, duration, driver);
  }

  public ReactiveDistributedLock createLock(String lockId) {
    return createLock(lockId, defaultDuration);
  }

  public ReactiveDistributedLock createLock(String lockId, Duration duration) {
    return new ReactiveDistributedSingleEntrantLock(
        LockId.of(lockId), instanceId, duration, driver);
  }

  public ReactiveDistributedLock createOverridingLock(String lockId) {
    return createOverridingLock(lockId, defaultDuration);
  }

  public ReactiveDistributedLock createOverridingLock(String lockId, Duration duration) {
    return new ReactiveDistributedOverridingLock(LockId.of(lockId), instanceId, duration, driver);
  }

  public static DistributedLocksBuilder builder(ReactiveDistributedLockDriver driver) {
    return new DistributedLocksBuilder(driver);
  }

  public static final class DistributedLocksBuilder {
    private final ReactiveDistributedLockDriver driver;
    private Duration defaultDuration = Duration.ofMinutes(5);
    private InstanceId instanceId = uniqueInstanceId();

    private DistributedLocksBuilder(ReactiveDistributedLockDriver driver) {
      this.driver = expectNonNull(driver, "Expected non null distributed locks driver");
    }

    public DistributedLocksBuilder withServiceInstanceId(
        String instanceId) {
      this.instanceId = InstanceId.of(instanceId);
      return this;
    }

    public DistributedLocksBuilder withLockDuration(
        Duration duration) {
      this.defaultDuration = expectNonNull(duration, "Expected non null duration");
      return this;
    }

    public ReactiveDistributedLocks build() {
      return new ReactiveDistributedLocks(driver, instanceId, defaultDuration);
    }
  }
}
