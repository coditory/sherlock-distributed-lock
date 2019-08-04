package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

import static com.coditory.sherlock.util.Preconditions.expectNonNull;

class ReactiveDelegatingDistributedLock implements ReactiveDistributedLock {
  private final LockId lockId;
  private final OwnerId ownerId;
  private final LockDuration duration;
  private final AcquireAction acquireAction;
  private final ReleaseAction releaseAction;

  ReactiveDelegatingDistributedLock(
    AcquireAction acquireAction,
    ReleaseAction releaseAction,
    LockId lockId,
    OwnerId ownerId,
    LockDuration duration) {
    this.lockId = expectNonNull(lockId);
    this.ownerId = expectNonNull(ownerId);
    this.duration = expectNonNull(duration);
    this.acquireAction = acquireAction;
    this.releaseAction = releaseAction;
  }

  @Override
  public String getId() {
    return lockId.getValue();
  }

  @Override
  public Publisher<AcquireResult> acquire() {
    return acquireAction.acquire(new LockRequest(lockId, ownerId, duration));
  }

  @Override
  public Publisher<AcquireResult> acquire(Duration duration) {
    LockDuration lockDuration = LockDuration.of(duration);
    return acquireAction.acquire(new LockRequest(lockId, ownerId, lockDuration));
  }

  @Override
  public Publisher<AcquireResult> acquireForever() {
    return acquireAction.acquire(new LockRequest(lockId, ownerId, null));
  }

  @Override
  public Publisher<ReleaseResult> release() {
    return releaseAction.release(lockId, ownerId);
  }

  @FunctionalInterface
  public interface ReleaseAction {
    Publisher<ReleaseResult> release(LockId lockId, OwnerId ownerId);
  }

  @FunctionalInterface
  public interface AcquireAction {
    Publisher<AcquireResult> acquire(LockRequest lockRequest);
  }
}
