package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Single;

import java.time.Duration;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class RxDelegatingDistributedLock implements RxDistributedLock {
    private final LockResultLogger lockResultLogger;
    private final LockId lockId;
    private final OwnerId ownerId;
    private final LockDuration duration;
    private final AcquireAction acquireAction;
    private final ReleaseAction releaseAction;

    RxDelegatingDistributedLock(
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
        this.lockResultLogger = new LockResultLogger(lockId.getValue(), getClass());
    }

    @Override
    public String getId() {
        return lockId.getValue();
    }

    @Override
    public Single<AcquireResult> acquire() {
        return acquire(new LockRequest(lockId, ownerId, duration));
    }

    @Override
    public Single<AcquireResult> acquire(Duration duration) {
        LockDuration lockDuration = LockDuration.of(duration);
        return acquire(new LockRequest(lockId, ownerId, lockDuration));
    }

    @Override
    public Single<AcquireResult> acquireForever() {
        return acquire(new LockRequest(lockId, ownerId, null));
    }

    @Override
    public Single<ReleaseResult> release() {
        return releaseAction.release(lockId, ownerId)
            .doAfterSuccess(lockResultLogger::logResult);
    }

    private Single<AcquireResult> acquire(LockRequest lockRequest) {
        return acquireAction.acquire(lockRequest)
            .doAfterSuccess(lockResultLogger::logResult);
    }

    @FunctionalInterface
    public interface ReleaseAction {
        Single<ReleaseResult> release(LockId lockId, OwnerId ownerId);
    }

    @FunctionalInterface
    public interface AcquireAction {
        Single<AcquireResult> acquire(LockRequest lockRequest);
    }
}
