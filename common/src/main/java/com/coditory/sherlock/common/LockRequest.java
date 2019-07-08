package com.coditory.sherlock.common;

import java.time.Duration;
import java.util.Objects;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

public final class LockRequest {
  private final LockId lockId;
  private final InstanceId instanceId;
  private final Duration duration;

  public LockRequest(
      LockId lockId,
      InstanceId instanceId,
      Duration duration) {
    this.lockId = expectNonNull(lockId);
    this.instanceId = expectNonNull(instanceId);
    this.duration = duration;
  }

  public LockRequest(
      LockId lockId,
      InstanceId instanceId) {
    this.lockId = expectNonNull(lockId);
    this.instanceId = expectNonNull(instanceId);
    this.duration = null;
  }

  public LockId getLockId() {
    return lockId;
  }

  public InstanceId getInstanceId() {
    return instanceId;
  }

  public Duration getDuration() {
    return duration;
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
        Objects.equals(instanceId, request.instanceId) &&
        Objects.equals(duration, request.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockId, instanceId, duration);
  }

  @Override
  public String toString() {
    return "LockRequest{" +
        "lockId=" + lockId +
        ", instanceId=" + instanceId +
        ", duration=" + duration +
        '}';
  }
}
