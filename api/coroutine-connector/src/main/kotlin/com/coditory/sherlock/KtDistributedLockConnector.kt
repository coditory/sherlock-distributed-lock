package com.coditory.sherlock

interface KtDistributedLockConnector {
    /**
     * Initializes underlying infrastructure for locks.
     * Most frequently triggers database table creation and index creation.
     *
     *
     * If it is not executed explicitly, connector may execute it during first acquire acquisition or
     * release.
     */
    suspend fun initialize()

    /**
     * Acquire a lock.
     */
    suspend fun acquire(lockRequest: LockRequest): Boolean

    /**
     * Acquire a lock or prolong it if it was acquired by the same instance.
     */
    suspend fun acquireOrProlong(lockRequest: LockRequest): Boolean

    /**
     * Acquire a lock even if it was already acquired by someone else
     */
    suspend fun forceAcquire(lockRequest: LockRequest): Boolean

    /**
     * Unlock a lock if wat acquired by the same instance.
     */
    suspend fun release(
        lockId: LockId,
        ownerId: OwnerId,
    ): Boolean

    /**
     * Release a lock without checking its owner or release date.
     */
    suspend fun forceRelease(lockId: LockId): Boolean

    /**
     * Release all locks without checking their owners or release dates.
     */
    suspend fun forceReleaseAll(): Boolean
}
