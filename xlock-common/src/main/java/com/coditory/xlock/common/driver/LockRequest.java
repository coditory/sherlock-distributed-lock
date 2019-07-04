package com.coditory.xlock.common.driver;

import com.coditory.xlock.common.LockInstanceId;
import com.coditory.xlock.common.ServiceInstanceId;
import com.coditory.xlock.common.LockId;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonEmpty;
import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

public class LockRequest {
  private final LockId lockId;
  private final LockInstanceId lockInstanceId;
  private final ServiceInstanceId serviceInstanceId;
  private final Duration duration;

  public LockRequest(LockId lockId, LockInstanceId lockInstanceId, ServiceInstanceId serviceInstanceId, Duration duration) {
    this.lockId = expectNonNull(lockId);
    this.lockInstanceId = expectNonNull(lockInstanceId);
    this.serviceInstanceId = expectNonNull(serviceInstanceId);
    this.duration = duration;
  }

  public LockRequest(LockId lockId, LockInstanceId lockInstanceId, ServiceInstanceId serviceInstanceId) {
    this(lockId, lockInstanceId, serviceInstanceId, null);
  }

  public LockId getLockId() {
    return lockId;
  }

  public ServiceInstanceId getServiceInstanceId() {
    return serviceInstanceId;
  }

  public LockInstanceId getLockInstanceId() {
    return lockInstanceId;
  }

  public Optional<Duration> getDuration() {
    return Optional.ofNullable(duration);
  }

  public Optional<Instant> expireAt(Instant now) {
    expectNonNull(now, "Expected non null now");
    return getDuration()
        .map(now::plus);
  }

  @Override
  public String toString() {
    return "LockRequest{" +
        "lockId=" + lockId +
        ", lockInstanceId=" + lockInstanceId +
        ", serviceInstanceId=" + serviceInstanceId +
        ", duration=" + duration +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LockRequest request = (LockRequest) o;
    return Objects.equals(lockId, request.lockId) &&
        Objects.equals(lockInstanceId, request.lockInstanceId) &&
        Objects.equals(serviceInstanceId, request.serviceInstanceId) &&
        Objects.equals(duration, request.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockId, lockInstanceId, serviceInstanceId, duration);
  }
}
