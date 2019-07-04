package com.coditory.xlock.common;

import java.time.Instant;
import java.util.Objects;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

public class LockState {
  private final LockId lockId;
  private final AcquisitionId acquisitionId;
  private final InstanceId instanceId;
  private final Instant acquireAt;
  private final Instant releaseAt;

  public LockState(LockId lockId, AcquisitionId acquisitionId, InstanceId instanceId, Instant createdAt, Instant expiresAt) {
    this.lockId = expectNonNull(lockId);
    this.acquisitionId = expectNonNull(acquisitionId);
    this.instanceId = expectNonNull(instanceId);
    this.acquireAt = expectNonNull(createdAt);
    this.releaseAt = expiresAt;
  }

  public LockId getLockId() {
    return lockId;
  }

  public AcquisitionId getAcquisitionId() {
    return acquisitionId;
  }

  public InstanceId getInstanceId() {
    return instanceId;
  }

  public Instant getAcquireAt() {
    return acquireAt;
  }

  public Instant getReleaseAt() {
    return releaseAt;
  }

  @Override
  public String toString() {
    return "LockState{" +
        "lockId=" + lockId +
        ", acquisitionId=" + acquisitionId +
        ", instanceId=" + instanceId +
        ", acquireAt=" + acquireAt +
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
        Objects.equals(acquisitionId, lockState.acquisitionId) &&
        Objects.equals(instanceId, lockState.instanceId) &&
        Objects.equals(acquireAt, lockState.acquireAt) &&
        Objects.equals(releaseAt, lockState.releaseAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockId, acquisitionId, instanceId, acquireAt, releaseAt);
  }
}
