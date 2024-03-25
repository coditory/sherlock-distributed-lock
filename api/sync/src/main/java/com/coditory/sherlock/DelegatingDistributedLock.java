package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class DelegatingDistributedLock implements DistributedLock {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
    }

    @Override
    @NotNull
    public String getId() {
        return lockId;
    }

    @Override
    public boolean acquire() {
        return acquire(new LockRequest(lockId, ownerId, duration));
    }

    @Override
    public boolean acquire(@NotNull Duration duration) {
        expectNonNull(duration, "duration");
        return acquire(new LockRequest(lockId, ownerId, duration));
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
        boolean release(@NotNull String lockId, @NotNull String ownerId);
    }

    @FunctionalInterface
    public interface AcquireAction {
        boolean acquire(@NotNull LockRequest lockRequest);
    }
}
