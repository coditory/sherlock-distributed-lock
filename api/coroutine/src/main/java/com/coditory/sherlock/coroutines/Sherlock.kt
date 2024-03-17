package com.coditory.sherlock.coroutines

import com.coditory.sherlock.DistributedLockBuilder
import com.coditory.sherlock.Preconditions

/**
 * Manages distributed locks using Reactor API.
 */
interface Sherlock {
    /**
     * Initializes underlying infrastructure. If this method is not invoked explicitly then it can be
     * invoked implicitly when acquiring or releasing a lock for the first time.
     *
     *
     * Most often initialization is related with creating indexes and tables.
     *
     * @return true if initialization was successful, otherwise false is returned
     */
    suspend fun initialize()

    /**
     * Creates a distributed lock. Created lock may be acquired only once by the same owner:
     *
     * <pre>`assert lock.acquire() == true
     * assert lock.acquire() == false
     * `</pre>
     *
     * @return the lock builder
     */
    fun createLock(): DistributedLockBuilder<DistributedLock>

    /**
     * Creates a lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the lock
     * @see Sherlock.createLock
     */
    fun createLock(lockId: String): DistributedLock {
        Preconditions.expectNonEmpty(lockId, "lockId")
        return createLock().withLockId(lockId).build()
    }

    /**
     * Creates a distributed reentrant lock. Reentrant lock may be acquired multiple times by the same
     * owner:
     *
     * <pre>`assert reentrantLock.acquire() == true
     * assert reentrantLock.acquire() == true
     * `</pre>
     *
     * @return the reentrant lock builder
     */
    fun createReentrantLock(): DistributedLockBuilder<DistributedLock>

    /**
     * Creates a reentrant lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the reentrant lock
     * @see Sherlock.createReentrantLock
     */
    fun createReentrantLock(lockId: String): DistributedLock {
        Preconditions.expectNonEmpty(lockId, "lockId")
        return createReentrantLock().withLockId(lockId).build()
    }

    /**
     * Create a distributed overriding lock. Returned lock may acquire or release any other lock
     * without checking its state:
     *
     * <pre>`assert someLock.acquire() == true
     * assert overridingLock.acquire() == true
     * `</pre>
     *
     *
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
     * @see Sherlock.createOverridingLock
     */
    fun createOverridingLock(lockId: String): DistributedLock {
        Preconditions.expectNonEmpty(lockId, "lockId")
        return createOverridingLock().withLockId(lockId).build()
    }

    /**
     * Force releases all acquired locks.
     *
     *
     * It could be used for administrative actions.
     *
     * @return true if lock was released, otherwise false is returned
     */
    suspend fun forceReleaseAllLocks(): Boolean

    /**
     * Force releases a lock.
     *
     *
     * It could be used for administrative actions.
     *
     * @param lockId lock identifier
     * @return true if lock was released, otherwise false is returned
     */
    suspend fun forceReleaseLock(lockId: String): Boolean {
        Preconditions.expectNonEmpty(lockId, "lockId")
        return createOverridingLock(lockId).release()
    }
}
