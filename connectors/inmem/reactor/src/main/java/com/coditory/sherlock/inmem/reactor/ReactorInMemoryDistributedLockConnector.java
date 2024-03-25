package com.coditory.sherlock.inmem.reactor;

import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import com.coditory.sherlock.inmem.InMemoryDistributedLockStorage;
import com.coditory.sherlock.reactor.DistributedLockConnector;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class ReactorInMemoryDistributedLockConnector implements DistributedLockConnector {
    private final InMemoryDistributedLockStorage storage;
    private final Clock clock;

    ReactorInMemoryDistributedLockConnector(Clock clock, InMemoryDistributedLockStorage storage) {
        this.clock = clock;
        this.storage = storage;
    }

    @Override
    @NotNull
    public Mono<InitializationResult> initialize() {
        return Mono.just(InitializationResult.initialized());
    }

    @Override
    @NotNull
    public Mono<AcquireResult> acquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return Mono.fromCallable(() -> storage.acquire(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    @NotNull
    public Mono<AcquireResult> acquireOrProlong(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return Mono.just(storage.acquireOrProlong(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    @NotNull
    public Mono<AcquireResult> forceAcquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return Mono.fromCallable(() -> storage.forceAcquire(lockRequest, now()))
            .map(AcquireResult::of);
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> release(@NotNull String lockId, @NotNull String ownerId) {
        expectNonNull(lockId, "lockId");
        expectNonNull(ownerId, "ownerId");
        return Mono.fromCallable(() -> storage.release(lockId, now(), ownerId))
            .map(ReleaseResult::of);
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> forceRelease(@NotNull String lockId) {
        expectNonNull(lockId, "lockId");
        return Mono.fromCallable(() -> storage.forceRelease(lockId, now()))
            .map(ReleaseResult::of);
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> forceReleaseAll() {
        return Mono.fromCallable(() -> storage.forceReleaseAll(now()))
            .map(ReleaseResult::of);
    }

    private Instant now() {
        return clock.instant();
    }
}
