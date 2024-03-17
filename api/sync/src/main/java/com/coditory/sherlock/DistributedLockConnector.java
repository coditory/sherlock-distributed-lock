package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

public interface DistributedLockConnector {
    /**
     * Initializes underlying infrastructure for locks.
     * Most frequently triggers database table creation and index creation.
     * <p>
     * If it is not executed explicitly, connector may execute it during first acquire acquisition or
     * release.
     */
    void initialize();

    /**
     * Acquire a lock.
     *
     * @return boolean - true if acquire was acquired by this call
     */
    boolean acquire(@NotNull LockRequest lockRequest);

    /**
     * Acquire a lock or prolong it if it was acquired by the same instance.
     *
     * @return boolean - true if acquire was acquired by this call
     */
    boolean acquireOrProlong(@NotNull LockRequest lockRequest);

    /**
     * Acquire a lock even if it was already acquired by someone else
     *
     * @return boolean - true if acquire was acquired by this call
     */
    boolean forceAcquire(@NotNull LockRequest lockRequest);

    /**
     * Unlock a lock if wat acquired by the same instance.
     *
     * @return boolean - true if acquire was released by this call
     */
    boolean release(@NotNull LockId lockId, @NotNull OwnerId ownerId);

    /**
     * Release a lock without checking its owner or release date.
     *
     * @return boolean - true if acquire was released by this call
     */
    boolean forceRelease(@NotNull LockId lockId);

    /**
     * Release all locks without checking their owners or release dates.
     *
     * @return boolean - true if at least one lock was released
     */
    boolean forceReleaseAll();
}
