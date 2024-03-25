package com.coditory.sherlock.coroutines

import com.coditory.sherlock.DistributedLockBuilder
import com.coditory.sherlock.Preconditions.expectNonEmpty

/**
 * Manages distributed locks using Kotlin Coroutines API.
 */
interface Sherlock {
    /**
     * Initializes underlying infrastructure. If this method is not invoked explicitly then it can be
     * invoked implicitly when acquiring or releasing a lock for the first time.
     * <p>
     * Initialization creates indexes and tables.
     *
     * @return true if initialization was successful, otherwise false is returned
     */
    suspend fun initialize()

    /**
     * Creates a distributed single-entrant lock builder.
     * Single-entrant lock can be acquired only once.
     * Even the same owner cannot acquire the same lock again.
     *
     * <pre>{@code
     * assert lock.acquire() == true
     * assert lock.acquire() == false
     * }</pre>
     *
     * @return the lock builder
     */
    fun createLock(): DistributedLockBuilder<DistributedLock>

    /**
     * Creates a single-entrant lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the lock
     * @see Sherlock#createLock()
     */
    fun createLock(lockId: String): DistributedLock {
        expectNonEmpty(lockId, "lockId")
        return createLock().withLockId(lockId).build()
    }

    /**
     * Creates a distributed reentrant lock.
     * Reentrant lock may be acquired multiple times by the same
     * owner:
     *
     * <pre>{@code
     * assert reentrantLock.acquire() == true
     * assert reentrantLock.acquire() == true
     * }</pre>
     *
     * @return the reentrant lock builder
     */
    fun createReentrantLock(): DistributedLockBuilder<DistributedLock>

    /**
     * Creates a reentrant lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the reentrant lock
     * @see Sherlock#createReentrantLock()
     */
    fun createReentrantLock(lockId: String): DistributedLock {
        expectNonEmpty(lockId, "lockId")
        return createReentrantLock().withLockId(lockId).build()
    }

    /**
     * Create a distributed overriding lock.
     * Returned lock may acquire or release any other lock
     * without checking its state:
     *
     * <pre>{@code
     * assert someLock.acquire() == true
     * assert overridingLock.acquire() == true
     * }</pre>
     * <p>
     * It could be used for administrative actions.
     *
     * @return the overriding lock builder
     */
    fun createOverridingLock(): DistributedLockBuilder<DistributedLock>

    /**
     * Creates an overriding lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the overriding lock
     * @see Sherlock#createOverridingLock()
     */
    fun createOverridingLock(lockId: String): DistributedLock {
        expectNonEmpty(lockId, "lockId")
        return createOverridingLock().withLockId(lockId).build()
    }

    /**
     * Force releases all acquired locks.
     * <p>
     * It could be used for administrative actions.
     *
     * @return true if lock was released, otherwise false is returned
     */
    suspend fun forceReleaseAllLocks(): Boolean

    /**
     * Force releases a lock.
     * <p>
     * It could be used for administrative actions.
     *
     * @param lockId lock identifier
     * @return true if lock was released, otherwise false is returned
     */
    suspend fun forceReleaseLock(lockId: String): Boolean {
        expectNonEmpty(lockId, "lockId")
        return createOverridingLock(lockId).release()
    }
}
