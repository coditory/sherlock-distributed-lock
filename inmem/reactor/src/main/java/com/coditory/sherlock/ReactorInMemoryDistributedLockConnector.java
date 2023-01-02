package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;

class ReactorInMemoryDistributedLockConnector implements ReactorDistributedLockConnector {
    private final InMemoryDistributedLockStorage storage;
    private final Clock clock;

    ReactorInMemoryDistributedLockConnector(Clock clock, InMemoryDistributedLockStorage storage) {
        this.clock = clock;
        this.storage = storage;
    }

    @Override
    public Mono<InitializationResult> initialize() {
        return Mono.just(InitializationResult.of(true));
    }

    @Override
    public Mono<AcquireResult> acquire(LockRequest lockRequest) {
        return Mono.fromCallable(() -> storage.acquire(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    public Mono<AcquireResult> acquireOrProlong(LockRequest lockRequest) {
        return Mono.just(storage.acquireOrProlong(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    public Mono<AcquireResult> forceAcquire(LockRequest lockRequest) {
        return Mono.fromCallable(() -> storage.forceAcquire(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    public Mono<ReleaseResult> release(LockId lockId, OwnerId ownerId) {
        return Mono.fromCallable(() -> storage.release(lockId, now(), ownerId))
            .map(ReleaseResult::of);
    }

    @Override
    public Mono<ReleaseResult> forceRelease(LockId lockId) {
        return Mono.fromCallable(() -> storage.forceRelease(lockId, now()))
            .map(ReleaseResult::of);
    }

    @Override
    public Mono<ReleaseResult> forceReleaseAll() {
        return Mono.fromCallable(() -> storage.forceReleaseAll(now()))
            .map(ReleaseResult::of);
    }

    private Instant now() {
        return clock.instant();
    }
}
