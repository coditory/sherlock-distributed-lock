package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.LockRequest;
import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.reactive.connector.AcquireResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class ReactiveDistributedReentrantLock implements ReactiveDistributedLock {
  private final LockId lockId;
  private final OwnerId ownerId;
  private final LockDuration duration;
  private final ReactiveDistributedLockConnector connector;

  ReactiveDistributedReentrantLock(
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
  public Publisher<AcquireResult> acquire() {
    return tryLock(duration);
  }

  @Override
  public Publisher<AcquireResult> acquire(Duration duration) {
    return tryLock(LockDuration.of(duration));
  }

  @Override
  public Publisher<AcquireResult> acquireForever() {
    return tryLock(null);
  }

  private Publisher<AcquireResult> tryLock(LockDuration duration) {
    LockRequest lockRequest = new LockRequest(lockId, ownerId, duration);
    return connector.acquireOrProlong(lockRequest);
  }

  @Override
  public Publisher<ReleaseResult> release() {
    return connector.release(lockId, ownerId);
  }
}
