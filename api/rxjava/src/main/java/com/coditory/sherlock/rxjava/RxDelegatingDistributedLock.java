package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.LockDuration;
import com.coditory.sherlock.LockId;
import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.OwnerId;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.LockResultLogger;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;

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
            LockDuration duration
    ) {
        this.lockId = expectNonNull(lockId, "lockId");
        this.ownerId = expectNonNull(ownerId, "ownerId");
        this.duration = expectNonNull(duration, "duration");
        this.acquireAction = expectNonNull(acquireAction, "acquireAction");
        this.releaseAction = expectNonNull(releaseAction, "releaseAction");
        this.lockResultLogger = new LockResultLogger(lockId.getValue(), getClass());
    }

    @Override
    @NotNull
    public String getId() {
        return lockId.getValue();
    }

    @Override
    @NotNull
    public Single<AcquireResult> acquire() {
        return acquire(new LockRequest(lockId, ownerId, duration));
    }

    @Override
    @NotNull
    public Single<AcquireResult> acquire(@NotNull Duration duration) {
        LockDuration lockDuration = LockDuration.of(duration);
        return acquire(new LockRequest(lockId, ownerId, lockDuration));
    }

    @Override
    @NotNull
    public Single<AcquireResult> acquireForever() {
        return acquire(new LockRequest(lockId, ownerId, null));
    }

    @Override
    @NotNull
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
        @NotNull
        Single<ReleaseResult> release(@NotNull LockId lockId, @NotNull OwnerId ownerId);
    }

    @FunctionalInterface
    public interface AcquireAction {
        @NotNull
        Single<AcquireResult> acquire(@NotNull LockRequest lockRequest);
    }
}
