package com.coditory.sherlock.reactor;

import com.coditory.sherlock.LockDuration;
import com.coditory.sherlock.LockId;
import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.OwnerId;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.LockResultLogger;
import com.coditory.sherlock.connector.ReleaseResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class ReactorDelegatingDistributedLock implements ReactorDistributedLock {
    private final LockResultLogger lockResultLogger;
    private final LockId lockId;
    private final OwnerId ownerId;
    private final LockDuration duration;
    private final AcquireAction acquireAction;
    private final ReleaseAction releaseAction;

    ReactorDelegatingDistributedLock(
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
    public Mono<AcquireResult> acquire() {
        return acquire(new LockRequest(lockId, ownerId, duration));
    }

    @Override
    @NotNull
    public Mono<AcquireResult> acquire(@NotNull Duration duration) {
        expectNonNull(duration, "duration");
        LockDuration lockDuration = LockDuration.of(duration);
        return acquire(new LockRequest(lockId, ownerId, lockDuration));
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
        Mono<ReleaseResult> release(@NotNull LockId lockId, @NotNull OwnerId ownerId);
    }

    @FunctionalInterface
    public interface AcquireAction {
        @NotNull
        Mono<AcquireResult> acquire(@NotNull LockRequest lockRequest);
    }
}
