package com.coditory.sherlock.common;

import java.time.Duration;
import java.util.Objects;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

public final class LockRequest {
  private final LockId lockId;
  private final OwnerId ownerId;
  private final Duration duration;

  public LockRequest(
      LockId lockId,
      OwnerId ownerId,
      Duration duration) {
    this.lockId = expectNonNull(lockId);
    this.ownerId = expectNonNull(ownerId);
    this.duration = duration;
  }

  public LockRequest(
      LockId lockId,
      OwnerId ownerId) {
    this.lockId = expectNonNull(lockId);
    this.ownerId = expectNonNull(ownerId);
    this.duration = null;
  }

  public LockId getLockId() {
    return lockId;
  }

  public OwnerId getOwnerId() {
    return ownerId;
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
        Objects.equals(ownerId, request.ownerId) &&
        Objects.equals(duration, request.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockId, ownerId, duration);
  }

  @Override
  public String toString() {
    return "LockRequest{" +
        "lockId=" + lockId +
        ", ownerId=" + ownerId +
        ", duration=" + duration +
        '}';
  }
}
