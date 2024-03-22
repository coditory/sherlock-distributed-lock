package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class DelegatingDistributedLock implements DistributedLock {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LockId lockId;
    private final OwnerId ownerId;
    private final LockDuration duration;
    private final AcquireAction acquireAction;
    private final ReleaseAction releaseAction;

    DelegatingDistributedLock(
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
    }

    @Override
    @NotNull
    public String getId() {
        return lockId.getValue();
    }

    @Override
    public boolean acquire() {
        return acquire(new LockRequest(lockId, ownerId, duration));
    }

    @Override
    public boolean acquire(@NotNull Duration duration) {
        expectNonNull(duration, "duration");
        LockDuration lockDuration = LockDuration.of(duration);
        return acquire(new LockRequest(lockId, ownerId, lockDuration));
    }

    @Override
    public boolean acquireForever() {
        return acquire(new LockRequest(lockId, ownerId, null));
    }

    @Override
    public boolean release() {
        boolean released = releaseAction.release(lockId, ownerId);
        if (released) {
            logger.debug("Lock released: {}", lockId);
        } else {
            logger.debug("Lock not released: {}", lockId);
        }
        return released;
    }

    private boolean acquire(@NotNull LockRequest lockRequest) {
        boolean acquired = acquireAction.acquire(lockRequest);
        if (acquired) {
            logger.debug("Lock acquired: {}, {}", lockId, lockRequest);
        } else {
            logger.debug("Lock not acquired: {}, {}", lockId, lockRequest);
        }
        return acquired;
    }

    @Override
    public String toString() {
        return "DelegatingDistributedLock{" +
                "lockId=" + lockId +
                ", ownerId=" + ownerId +
                ", duration=" + duration +
                '}';
    }

    @FunctionalInterface
    public interface ReleaseAction {
        boolean release(@NotNull LockId lockId, @NotNull OwnerId ownerId);
    }

    @FunctionalInterface
    public interface AcquireAction {
        boolean acquire(@NotNull LockRequest lockRequest);
    }
}
