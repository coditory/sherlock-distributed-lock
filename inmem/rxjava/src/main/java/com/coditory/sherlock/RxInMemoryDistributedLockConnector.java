package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Single;

import java.time.Clock;
import java.time.Instant;

class RxInMemoryDistributedLockConnector implements RxDistributedLockConnector {
    private final InMemoryDistributedLockStorage storage;
    private final Clock clock;

    RxInMemoryDistributedLockConnector(Clock clock, InMemoryDistributedLockStorage storage) {
        this.clock = clock;
        this.storage = storage;
    }

    @Override
    public Single<InitializationResult> initialize() {
        return Single.just(InitializationResult.of(true));
    }

    @Override
    public Single<AcquireResult> acquire(LockRequest lockRequest) {
        return Single.fromCallable(() -> storage.acquire(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    public Single<AcquireResult> acquireOrProlong(LockRequest lockRequest) {
        return Single.just(storage.acquireOrProlong(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    public Single<AcquireResult> forceAcquire(LockRequest lockRequest) {
        return Single.fromCallable(() -> storage.forceAcquire(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    public Single<ReleaseResult> release(LockId lockId, OwnerId ownerId) {
        return Single.fromCallable(() -> storage.release(lockId, now(), ownerId))
            .map(ReleaseResult::of);
    }

    @Override
    public Single<ReleaseResult> forceRelease(LockId lockId) {
        return Single.fromCallable(() -> storage.forceRelease(lockId, now()))
            .map(ReleaseResult::of);
    }

    @Override
    public Single<ReleaseResult> forceReleaseAll() {
        return Single.fromCallable(() -> storage.forceReleaseAll(now()))
            .map(ReleaseResult::of);
    }

    private Instant now() {
        return clock.instant();
    }
}
