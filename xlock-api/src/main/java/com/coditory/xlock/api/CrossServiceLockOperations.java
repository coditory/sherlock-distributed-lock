package com.coditory.xlock.api;

import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.LockInstanceId;
import com.coditory.xlock.common.LockState;
import com.coditory.xlock.common.ServiceInstanceId;
import com.coditory.xlock.common.driver.LockRequest;
import com.coditory.xlock.common.driver.LockResult;
import com.coditory.xlock.common.driver.UnlockResult;
import com.coditory.xlock.common.driver.XLockDriver;

import java.time.Duration;
import java.util.Optional;

import static com.coditory.xlock.common.LockInstanceId.uniqueLockInstanceId;
import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

public class CrossServiceLockOperations {
  private final LockInstanceId lockInstanceId = uniqueLockInstanceId();
  private final LockId lockId;
  private final ServiceInstanceId serviceInstanceId;
  private final Duration duration;
  private final XLockDriver driver;

  CrossServiceLockOperations(
      LockId lockId, ServiceInstanceId serviceInstanceId, Duration duration, XLockDriver driver) {
    this.lockId = expectNonNull(lockId);
    this.serviceInstanceId = expectNonNull(serviceInstanceId);
    this.duration = expectNonNull(duration);
    this.driver = expectNonNull(driver);
  }

  public CrossServiceLockOperations newInstance() {
    return new CrossServiceLockOperations(lockId, serviceInstanceId, duration, driver);
  }

  public CrossServiceLockResult lock() {
    LockRequest request = new LockRequest(lockId, lockInstanceId, serviceInstanceId, duration);
    return toActionableResult(driver.lock(request));
  }

  public CrossServiceLockResult lock(Duration duration) {
    LockRequest request = new LockRequest(lockId, lockInstanceId, serviceInstanceId, duration);
    return toActionableResult(driver.lock(request));
  }

  public CrossServiceLockResult lockInfinitely() {
    LockRequest request = new LockRequest(lockId, lockInstanceId, serviceInstanceId);
    return toActionableResult(driver.lock(request));
  }

  public CrossServiceLockResult forceLock() {
    LockRequest request = new LockRequest(lockId, lockInstanceId, serviceInstanceId, duration);
    return toActionableResult(driver.forceLock(request));
  }

  public CrossServiceLockResult forceLock(Duration duration) {
    LockRequest request = new LockRequest(lockId, lockInstanceId, serviceInstanceId, duration);
    return toActionableResult(driver.forceLock(request));
  }

  public CrossServiceLockResult forceLockInfinitely() {
    LockRequest request = new LockRequest(lockId, lockInstanceId, serviceInstanceId);
    return toActionableResult(driver.forceLock(request));
  }

  public UnlockResult unlock() {
    return driver.unlock(lockInstanceId);
  }

  public UnlockResult forceUnlock() {
    return driver.forceUnlock(lockId);
  }

  public Optional<LockState> getLockState() {
    return driver.getLockState(lockId);
  }

  public LockInstanceId getLockInstanceId() {
    return lockInstanceId;
  }

  public LockId getLockId() {
    return lockId;
  }

  public ServiceInstanceId getServiceInstanceId() {
    return serviceInstanceId;
  }

  public Duration getDuration() {
    return duration;
  }

  private CrossServiceLockResult toActionableResult(LockResult result) {
    return result.isLockGranted()
        ? new CrossServiceLockGranted(this)
        : new CrossServiceLockRefused();
  }
}
