package com.coditory.sherlock.reactor;

import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.LockResultLogger;
import com.coditory.sherlock.connector.ReleaseResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
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
        this.lockId = expectNonEmpty(lockId, "lockId");
        this.ownerId = expectNonEmpty(ownerId, "ownerId");
        this.duration = duration;
        this.acquireAction = expectNonNull(acquireAction, "acquireAction");
        this.releaseAction = expectNonNull(releaseAction, "releaseAction");
        this.lockResultLogger = new LockResultLogger(lockId, getClass());
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
    public String getId() {
        return lockId;
    }

    @Override
    @NotNull
    public Mono<AcquireResult> acquire() {
        return acquire(new LockRequest(lockId, ownerId, duration));
    }

    @Override
    @NotNull
    public Mono<AcquireResult> acquire(@NotNull Duration duration) {
        expectNonNull(duration, "duration");
        return acquire(new LockRequest(lockId, ownerId, duration));
    }

    @Override
    @NotNull
    public Mono<AcquireResult> acquireForever() {
        return acquire(new LockRequest(lockId, ownerId, null));
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> release() {
        return releaseAction.release(lockId, ownerId)
            .doOnNext(lockResultLogger::logResult);
    }

    private Mono<AcquireResult> acquire(LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return acquireAction.acquire(lockRequest)
            .doOnNext(lockResultLogger::logResult);
    }

    @FunctionalInterface
    public interface ReleaseAction {
        @NotNull
        Mono<ReleaseResult> release(@NotNull String lockId, @NotNull String ownerId);
    }

    @FunctionalInterface
    public interface AcquireAction {
        @NotNull
        Mono<AcquireResult> acquire(@NotNull LockRequest lockRequest);
    }
}
