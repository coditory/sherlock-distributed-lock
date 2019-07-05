package com.coditory.xlock.api;

import com.coditory.xlock.common.InstanceId;
import com.coditory.xlock.common.driver.DistributedLockDriver;
import com.coditory.xlock.common.util.Preconditions;

import java.time.Duration;

import static com.coditory.xlock.common.InstanceId.uniqueInstanceId;
import static com.coditory.xlock.common.util.Preconditions.expectNonNull;

class DistributedLocksBuilder {
  private final DistributedLockDriver driver;
  private Duration defaultDuration = Duration.ofMinutes(5);
  private InstanceId instanceId = uniqueInstanceId();

  public DistributedLocksBuilder(DistributedLockDriver driver) {
    this.driver = expectNonNull(driver, "Expected non null distributed locks driver");
  }

  public DistributedLocksBuilder withServiceInstanceId(String instanceId) {
    this.instanceId = InstanceId.of(instanceId);
    return this;
  }

  public DistributedLocksBuilder withDefaultLockDurationd(Duration duration) {
    this.defaultDuration = expectNonNull(duration, "Expected non null duration");
    return this;
  }

  public DistributedLocks build() {
    return new DistributedLocks(driver, instanceId, defaultDuration);
  }
}
