package com.coditory.sherlock.inmem.rxjava;

import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import com.coditory.sherlock.inmem.InMemoryDistributedLockStorage;
import com.coditory.sherlock.rxjava.DistributedLockConnector;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class InMemoryDistributedLockConnector implements DistributedLockConnector {
    private final InMemoryDistributedLockStorage storage;
    private final Clock clock;

    InMemoryDistributedLockConnector(Clock clock, InMemoryDistributedLockStorage storage) {
        this.clock = clock;
        this.storage = storage;
    }

    @Override
    @NotNull
    public Single<InitializationResult> initialize() {
        return Single.just(InitializationResult.initializedResult());
    }

    @Override
    @NotNull
    public Single<AcquireResult> acquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return Single.fromCallable(() -> storage.acquire(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    @NotNull
    public Single<AcquireResult> acquireOrProlong(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return Single.just(storage.acquireOrProlong(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    @NotNull
    public Single<AcquireResult> forceAcquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return Single.fromCallable(() -> storage.forceAcquire(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    @NotNull
    public Single<ReleaseResult> release(@NotNull String lockId, @NotNull String ownerId) {
        expectNonNull(lockId, "lockId");
        expectNonNull(ownerId, "ownerId");
        return Single.fromCallable(() -> storage.release(lockId, now(), ownerId))
            .map(ReleaseResult::of);
    }

    @Override
    @NotNull
    public Single<ReleaseResult> forceRelease(@NotNull String lockId) {
        expectNonNull(lockId, "lockId");
        return Single.fromCallable(() -> storage.forceRelease(lockId, now()))
            .map(ReleaseResult::of);
    }

    @Override
    @NotNull
    public Single<ReleaseResult> forceReleaseAll() {
        return Single.fromCallable(() -> storage.forceReleaseAll(now()))
            .map(ReleaseResult::of);
    }

    private Instant now() {
        return clock.instant();
    }
}
