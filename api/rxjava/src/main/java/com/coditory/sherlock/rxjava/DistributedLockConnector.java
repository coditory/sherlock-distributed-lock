package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;

public interface DistributedLockConnector {
    /**
     * Initializes underlying infrastructure for locks.
     * Most frequently triggers database table creation and index creation.
     * <p>
     * If it is not executed explicitly, connector may execute it during first acquire acquisition or
     * release.
     */
    @NotNull
    Single<InitializationResult> initialize();

    /**
     * Acquire a lock.
     */
    @NotNull
    Single<AcquireResult> acquire(@NotNull LockRequest lockRequest);

    /**
     * Acquire a lock or prolong it if it was acquired by the same instance.
     */
    @NotNull
    Single<AcquireResult> acquireOrProlong(LockRequest lockRequest);

    /**
     * Acquire a lock even if it was already acquired by someone else
     */
    @NotNull
    Single<AcquireResult> forceAcquire(@NotNull LockRequest lockRequest);

    /**
     * Unlock a lock if wat acquired by the same instance.
     */
    @NotNull
    Single<ReleaseResult> release(@NotNull String lockId, @NotNull String ownerId);

    /**
     * Release a lock without checking its owner or release date.
     */
    @NotNull
    Single<ReleaseResult> forceRelease(@NotNull String lockId);

    /**
     * Release all locks without checking their owners or release dates.
     */
    @NotNull
    Single<ReleaseResult> forceReleaseAll();
}
