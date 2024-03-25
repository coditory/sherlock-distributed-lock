package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.LockResultLogger;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class DelegatingDistributedLock implements DistributedLock {
    private final LockResultLogger lockResultLogger;
    private final String lockId;
    private final String ownerId;
    private final Duration duration;
    private final AcquireAction acquireAction;
    private final ReleaseAction releaseAction;

    DelegatingDistributedLock(
        AcquireAction acquireAction,
        ReleaseAction releaseAction,
        String lockId,
        String ownerId,
        Duration duration
    ) {
        this.lockId = expectNonNull(lockId, "lockId");
        this.ownerId = expectNonNull(ownerId, "ownerId");
        this.duration = duration;
        this.acquireAction = expectNonNull(acquireAction, "acquireAction");
        this.releaseAction = expectNonNull(releaseAction, "releaseAction");
        this.lockResultLogger = new LockResultLogger(lockId, getClass());
    }

    @Override
    @NotNull
    public String getId() {
        return lockId;
    }

    @Override
    public String toString() {
        return "DelegatingDistributedLock{" +
            "lockId=" + lockId +
            ", ownerId=" + ownerId +
            ", duration=" + duration +
            '}';
    }

    @Override
    @NotNull
    public Single<AcquireResult> acquire() {
        return acquire(new LockRequest(lockId, ownerId, duration));
    }

    @Override
    @NotNull
    public Single<AcquireResult> acquire(@NotNull Duration duration) {
        return acquire(new LockRequest(lockId, ownerId, duration));
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
        Single<ReleaseResult> release(@NotNull String lockId, @NotNull String ownerId);
    }

    @FunctionalInterface
    public interface AcquireAction {
        @NotNull
        Single<AcquireResult> acquire(@NotNull LockRequest lockRequest);
    }
}
