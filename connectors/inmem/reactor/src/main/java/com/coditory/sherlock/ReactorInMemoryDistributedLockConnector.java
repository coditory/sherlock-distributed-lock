package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class ReactorInMemoryDistributedLockConnector implements ReactorDistributedLockConnector {
    private final InMemoryDistributedLockStorage storage;
    private final Clock clock;

    ReactorInMemoryDistributedLockConnector(Clock clock, InMemoryDistributedLockStorage storage) {
        this.clock = clock;
        this.storage = storage;
    }

    @Override
    @NotNull
    public Mono<InitializationResult> initialize() {
        return Mono.just(InitializationResult.of(true));
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
    public Mono<ReleaseResult> release(@NotNull LockId lockId, @NotNull OwnerId ownerId) {
        expectNonNull(lockId, "lockId");
        expectNonNull(ownerId, "ownerId");
        return Mono.fromCallable(() -> storage.release(lockId, now(), ownerId))
                .map(ReleaseResult::of);
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> forceRelease(@NotNull LockId lockId) {
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
