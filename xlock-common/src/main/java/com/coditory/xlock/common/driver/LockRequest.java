package com.coditory.xlock.common.driver;

import com.coditory.xlock.common.InstanceId;
import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.util.XLockPreconditions;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonEmpty;
import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

public class LockRequest {
  private final LockId lockId;
  private final InstanceId instanceId;
  private final Duration duration;

  public LockRequest(LockId lockId, InstanceId instanceId, Duration duration) {
    this.lockId = expectNonNull(lockId);
    this.instanceId = expectNonNull(instanceId);
    this.duration = duration;
  }

  public LockId getLockId() {
    return lockId;
  }

  public InstanceId getInstanceId() {
    return instanceId;
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
        ", instanceId=" + instanceId +
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
    LockRequest that = (LockRequest) o;
    return Objects.equals(lockId, that.lockId) &&
        Objects.equals(instanceId, that.instanceId) &&
        Objects.equals(duration, that.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockId, instanceId, duration);
  }
}
