package com.coditory.sherlock.inmem;

import com.coditory.sherlock.DistributedLockConnector;
import com.coditory.sherlock.LockId;
import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.OwnerId;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class InMemoryDistributedLockConnector implements DistributedLockConnector {
    private final InMemoryDistributedLockStorage storage;
    private final Clock clock;

    InMemoryDistributedLockConnector(Clock clock, InMemoryDistributedLockStorage storage) {
        this.storage = storage;
        this.clock = clock;
    }

    @Override
    public void initialize() {
        // deliberately empty
    }

    @Override
    synchronized public boolean acquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return storage.acquire(lockRequest, now());
    }

    @Override
    synchronized public boolean acquireOrProlong(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return storage.acquireOrProlong(lockRequest, now());
    }

    @Override
    synchronized public boolean forceAcquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return storage.forceAcquire(lockRequest, now());
    }

    @Override
    synchronized public boolean release(@NotNull LockId lockId, @NotNull OwnerId ownerId) {
        expectNonNull(lockId, "lockId");
        expectNonNull(ownerId, "ownerId");
        return storage.release(lockId, now(), ownerId);
    }

    @Override
    synchronized public boolean forceRelease(@NotNull LockId lockId) {
        expectNonNull(lockId, "lockId");
        return storage.forceRelease(lockId, now());
    }

    @Override
    public boolean forceReleaseAll() {
        return storage.forceReleaseAll(now());
    }

    private Instant now() {
        return clock.instant();
    }
}
