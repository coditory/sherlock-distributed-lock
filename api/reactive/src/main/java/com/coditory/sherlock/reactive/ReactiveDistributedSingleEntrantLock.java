package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.LockRequest;
import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.reactive.connector.LockResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Flow.Publisher;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class ReactiveDistributedSingleEntrantLock implements ReactiveDistributedLock {
  private final LockId lockId;
  private final OwnerId ownerId;
  private final LockDuration duration;
  private final ReactiveDistributedLockConnector connector;

  ReactiveDistributedSingleEntrantLock(
      LockId lockId,
      OwnerId ownerId,
      LockDuration duration,
      ReactiveDistributedLockConnector connector) {
    this.lockId = expectNonNull(lockId);
    this.ownerId = expectNonNull(ownerId);
    this.duration = expectNonNull(duration);
    this.connector = expectNonNull(connector);
  }

  @Override
  public String getId() {
    return lockId.getValue();
  }

  @Override
  public Publisher<LockResult> acquire() {
    return tryLock(duration);
  }

  @Override
  public Publisher<LockResult> acquire(Duration duration) {
    return tryLock(LockDuration.of(duration));
  }

  @Override
  public Publisher<LockResult> acquireForever() {
    return tryLock(null);
  }

  private Publisher<LockResult> tryLock(LockDuration duration) {
    LockRequest lockRequest = new LockRequest(lockId, ownerId, duration);
    return connector.acquire(lockRequest);
  }

  @Override
  public Publisher<ReleaseResult> release() {
    return connector.release(lockId, ownerId);
  }

  @Override
  public String toString() {
    return "ReactiveDistributedSingleEntrantLock{" +
        "lockId=" + lockId +
        ", ownerId=" + ownerId +
        ", duration=" + duration +
        ", connector=" + connector +
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
    ReactiveDistributedSingleEntrantLock that = (ReactiveDistributedSingleEntrantLock) o;
    return Objects.equals(lockId, that.lockId) &&
        Objects.equals(ownerId, that.ownerId) &&
        Objects.equals(duration, that.duration) &&
        Objects.equals(connector, that.connector);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockId, ownerId, duration, connector);
  }
}
