package com.coditory.sherlock.reactor;

import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public interface DistributedLockConnector {
    /**
     * Initializes underlying infrastructure for locks.
     * Most frequently triggers database table creation and index creation.
     * <p>
     * If it is not executed explicitly, connector may execute it during first acquire acquisition or
     * release.
     */
    @NotNull
    Mono<InitializationResult> initialize();

    /**
     * Acquires a lock.
     */
    @NotNull
    Mono<AcquireResult> acquire(@NotNull LockRequest lockRequest);

    /**
     * Acquires a lock or prolongs it if it was acquired by the same instance.
     */
    @NotNull
    Mono<AcquireResult> acquireOrProlong(LockRequest lockRequest);

    /**
     * Acquires a lock even if it was already acquired by someone else.
     */
    @NotNull
    Mono<AcquireResult> forceAcquire(@NotNull LockRequest lockRequest);

    /**
     * Unlocks a lock if wat acquired by the same instance.
     */
    @NotNull
    Mono<ReleaseResult> release(@NotNull String lockId, @NotNull String ownerId);

    /**
     * Releases a lock without checking its owner or release date.
     */
    @NotNull
    Mono<ReleaseResult> forceRelease(@NotNull String lockId);

    /**
     * Releases all locks without checking their owners or release dates.
     */
    @NotNull
    Mono<ReleaseResult> forceReleaseAll();
}
