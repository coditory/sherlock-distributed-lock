package com.coditory.xlock.common;

import com.coditory.xlock.common.driver.LockRequest;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

public class LockState {
  public static LockState fromLockRequest(LockRequest lockRequest, Instant acquiredAt) {
    expectNonNull(lockRequest);
    expectNonNull(acquiredAt);
    Instant releaseAt = lockRequest.getDuration()
        .map(acquiredAt::plus)
        .orElse(null);
    return new LockState(
        lockRequest.getLockId(),
        lockRequest.getLockInstanceId(),
        lockRequest.getServiceInstanceId(),
        acquiredAt,
        releaseAt
    );
  }

  private final LockId lockId;
  private final LockInstanceId lockInstanceId;
  private final ServiceInstanceId serviceInstanceId;
  private final Instant acquiredAt;
  private final Instant releaseAt;

  public LockState(
      LockId lockId, LockInstanceId lockInstanceId, ServiceInstanceId serviceInstanceId,
      Instant createdAt, Instant expiresAt) {
    this.lockId = expectNonNull(lockId);
    this.lockInstanceId = expectNonNull(lockInstanceId);
    this.serviceInstanceId = expectNonNull(serviceInstanceId);
    this.acquiredAt = expectNonNull(createdAt);
    this.releaseAt = expiresAt;
  }

  public LockId getLockId() {
    return lockId;
  }

  public LockInstanceId getLockInstanceId() {
    return lockInstanceId;
  }

  public ServiceInstanceId getServiceInstanceId() {
    return serviceInstanceId;
  }

  public Instant getAcquiredAt() {
    return acquiredAt;
  }

  public Optional<Instant> getReleaseAt() {
    return Optional.ofNullable(releaseAt);
  }

  @Override
  public String toString() {
    return "LockState{" +
        "lockId=" + lockId +
        ", lockInstanceId=" + lockInstanceId +
        ", serviceInstanceId=" + serviceInstanceId +
        ", acquiredAt=" + acquiredAt +
        ", releaseAt=" + releaseAt +
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
    LockState lockState = (LockState) o;
    return Objects.equals(lockId, lockState.lockId) &&
        Objects.equals(lockInstanceId, lockState.lockInstanceId) &&
        Objects.equals(serviceInstanceId, lockState.serviceInstanceId) &&
        Objects.equals(acquiredAt, lockState.acquiredAt) &&
        Objects.equals(releaseAt, lockState.releaseAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockId, lockInstanceId, serviceInstanceId, acquiredAt, releaseAt);
  }
}
