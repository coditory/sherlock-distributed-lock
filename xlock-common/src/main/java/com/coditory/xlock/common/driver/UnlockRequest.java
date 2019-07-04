package com.coditory.xlock.common.driver;

import com.coditory.xlock.common.AcquisitionId;
import com.coditory.xlock.common.InstanceId;
import com.coditory.xlock.common.LockId;

import java.util.Objects;
import java.util.Optional;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

public class UnlockRequest {
  private final LockId lockId;
  private final AcquisitionId acquisitionId;
  private final InstanceId instanceId;

  public UnlockRequest(
      LockId lockId, AcquisitionId acquisitionId,
      InstanceId instanceId) {
    this.lockId = expectNonNull(lockId);
    this.acquisitionId = acquisitionId;
    this.instanceId = instanceId;
  }

  public LockId getLockId() {
    return lockId;
  }

  public Optional<AcquisitionId> getAcquisitionId() {
    return Optional.ofNullable(acquisitionId);
  }

  public Optional<InstanceId> getInstanceId() {
    return Optional.ofNullable(instanceId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UnlockRequest that = (UnlockRequest) o;
    return Objects.equals(lockId, that.lockId) &&
        Objects.equals(acquisitionId, that.acquisitionId) &&
        Objects.equals(instanceId, that.instanceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockId, acquisitionId, instanceId);
  }

  @Override
  public String toString() {
    return "UnlockRequest{" +
        "lockId=" + lockId +
        ", acquisitionId=" + acquisitionId +
        ", instanceId=" + instanceId +
        '}';
  }
}
