package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.LockRequest;
import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.reactive.driver.LockResult;
import com.coditory.sherlock.reactive.driver.ReleaseResult;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class ReactiveDistributedOverridingLock implements ReactiveDistributedLock {
  private final LockId lockId;
  private final OwnerId ownerId;
  private final LockDuration duration;
  private final ReactiveDistributedLockDriver driver;

  ReactiveDistributedOverridingLock(
      LockId lockId,
      OwnerId ownerId,
      LockDuration duration,
      ReactiveDistributedLockDriver driver) {
    this.lockId = expectNonNull(lockId);
    this.ownerId = expectNonNull(ownerId);
    this.duration = expectNonNull(duration);
    this.driver = expectNonNull(driver);
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
    return driver.forceAcquire(lockRequest);
  }

  @Override
  public Publisher<ReleaseResult> release() {
    return driver.forceRelease(lockId);
  }
}
