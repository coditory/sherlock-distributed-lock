package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
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
    public Mono<AcquireResult> acquire() {
        return acquire(new LockRequest(lockId, ownerId, duration));
    }

    @Override
    public Mono<AcquireResult> acquire(Duration duration) {
        LockDuration lockDuration = LockDuration.of(duration);
        return acquire(new LockRequest(lockId, ownerId, lockDuration));
    }

    @Override
    public Mono<AcquireResult> acquireForever() {
        return acquire(new LockRequest(lockId, ownerId, null));
    }

    @Override
    public Mono<ReleaseResult> release() {
        return releaseAction.release(lockId, ownerId)
            .doOnNext(lockResultLogger::logResult);
    }

    private Mono<AcquireResult> acquire(LockRequest lockRequest) {
        return acquireAction.acquire(lockRequest)
            .doOnNext(lockResultLogger::logResult);
    }

    @FunctionalInterface
    public interface ReleaseAction {
        Mono<ReleaseResult> release(LockId lockId, OwnerId ownerId);
    }

    @FunctionalInterface
    public interface AcquireAction {
        Mono<AcquireResult> acquire(LockRequest lockRequest);
    }
}
