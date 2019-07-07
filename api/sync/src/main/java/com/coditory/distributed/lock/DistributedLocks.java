package com.coditory.distributed.lock;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;

import java.time.Duration;

import static com.coditory.distributed.lock.common.InstanceId.uniqueInstanceId;
import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;

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

  public static DistributedLocksBuilder builder(DistributedLockDriver driver) {
    return new DistributedLocksBuilder(driver);
  }

  public static final class DistributedLocksBuilder {
    private final DistributedLockDriver driver;
    private Duration defaultDuration = Duration.ofMinutes(5);
    private InstanceId instanceId = uniqueInstanceId();

    private DistributedLocksBuilder(DistributedLockDriver driver) {
      this.driver = expectNonNull(driver, "Expected non null distributed locks driver");
    }

    public DistributedLocksBuilder withServiceInstanceId(
        String instanceId) {
      this.instanceId = InstanceId.of(instanceId);
      return this;
    }

    public DistributedLocksBuilder withDefaultLockDurationd(
        Duration duration) {
      this.defaultDuration = expectNonNull(duration, "Expected non null duration");
      return this;
    }

    public DistributedLocks build() {
      return new DistributedLocks(driver, instanceId, defaultDuration);
    }
  }
}
