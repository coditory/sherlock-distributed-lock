package com.coditory.xlock.api;

import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.ServiceInstanceId;
import com.coditory.xlock.common.driver.XLockDriver;

import java.time.Duration;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

public class LockFactory {
  private final XLockDriver driver;
  private final ServiceInstanceId serviceInstanceId;
  private final Duration defaultDuration;

  public LockFactory(XLockDriver driver, ServiceInstanceId serviceInstanceId) {
    this(driver, serviceInstanceId, Duration.ofMinutes(1));
  }

  public LockFactory(
      XLockDriver driver, ServiceInstanceId serviceInstanceId, Duration defaultDuration) {
    this.driver = expectNonNull(driver, "Expected non null driver");
    this.serviceInstanceId = expectNonNull(serviceInstanceId, "Expected non null instanceId");
    this.defaultDuration = expectNonNull(defaultDuration, "Expected non null defaultDuration");
  }

  public CrossServiceLock createTimeLimitedLock(LockId lockId) {
    expectNonNull(lockId, "Expected non null lockId");
    return createTimeLimitedLock(lockId, defaultDuration);
  }

  public CrossServiceLock createTimeLimitedLock(LockId lockId, Duration duration) {
    expectNonNull(lockId, "Expected non null lockId");
    expectNonNull(duration, "Expected non null duration");
    return new CrossServiceTimeLimitedLock(createLockOperations(lockId, duration));
  }

  public CrossServiceLock createInfiniteLock(LockId lockId) {
    expectNonNull(lockId, "Expected non null lockId");
    return new CrossServiceInfiniteLock(createLockOperations(lockId));
  }

  public CrossServiceLockOperations createLockOperations(LockId lockId) {
    expectNonNull(lockId, "Expected non null lockId");
    return createLockOperations(lockId, defaultDuration);
  }

  public CrossServiceLockOperations createLockOperations(LockId lockId, Duration duration) {
    expectNonNull(lockId, "Expected non null lockId");
    expectNonNull(duration, "Expected non null duration");
    return new CrossServiceLockOperations(lockId, serviceInstanceId, duration, driver);
  }

  public ServiceInstanceId getServiceInstanceId() {
    return serviceInstanceId;
  }

  public Duration getDefaultDuration() {
    return defaultDuration;
  }
}
