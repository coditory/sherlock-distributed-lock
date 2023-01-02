package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Mono;

interface ReactorDistributedLockConnector {
    /**
     * Initializes underlying infrastructure for locks. Most frequently triggers database index
     * creation.
     * <p>
     * If it is not executed explicitly, connector may execute it during first acquire acquisition or
     * release.
     */
    Mono<InitializationResult> initialize();

    /**
     * Acquires a acquire when there is no acquire acquired with the same lockId.
     */
    Mono<AcquireResult> acquire(LockRequest lockRequest);

    /**
     * Acquires a acquire when there is no acquire acquired with the same lockId. Prolongs the acquire
     * if it was already acquired by the same instance.
     */
    Mono<AcquireResult> acquireOrProlong(LockRequest lockRequest);

    /**
     * Acquires a acquire even if it was already acquired.
     */
    Mono<AcquireResult> forceAcquire(LockRequest lockRequest);

    /**
     * Unlock previously acquired lock by the same instance.
     */
    Mono<ReleaseResult> release(LockId lockId, OwnerId ownerId);

    /**
     * Releases a lock without checking its owner or release date.
     */
    Mono<ReleaseResult> forceRelease(LockId lockId);

    /**
     * Releases all locks without checking their owners or release dates.
     */
    Mono<ReleaseResult> forceReleaseAll();
}
