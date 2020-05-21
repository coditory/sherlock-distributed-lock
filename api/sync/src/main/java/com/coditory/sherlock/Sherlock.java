package com.coditory.sherlock;

/**
 * Manages distributed locks.
 */
public interface Sherlock {

    /**
     * Initializes underlying infrastructure. If this method is not invoked explicitly then it can be
     * invoked implicitly when acquiring or releasing a lock for the first time.
     * <p>
     * Most often initialization is related with creating indexes and tables.
     */
    void initialize();

    /**
     * Creates a distributed lock. Created lock may be acquired only once by the same owner:
     *
     * <pre>{@code
     * assert lock.acquire() == true
     * assert lock.acquire() == false
     * }</pre>
     *
     * @return the lock builder
     */
    DistributedLockBuilder<DistributedLock> createLock();

    /**
     * Creates a lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the lock
     * @see Sherlock#createLock()
     */
    default DistributedLock createLock(String lockId) {
        return createLock().withLockId(lockId).build();
    }

    /**
     * Creates a distributed reentrant lock. Reentrant lock may be acquired multiple times by the same
     * owner:
     *
     * <pre>{@code
     * assert reentrantLock.acquire() == true
     * assert reentrantLock.acquire() == true
     * }</pre>
     *
     * @return the reentrant lock builder
     */
    DistributedLockBuilder<DistributedLock> createReentrantLock();

    /**
     * Creates a reentrant lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the reentrant lock
     * @see Sherlock#createReentrantLock()
     */
    default DistributedLock createReentrantLock(String lockId) {
        return createReentrantLock().withLockId(lockId).build();
    }

    /**
     * Create a distributed overriding lock. Returned lock may acquire or release any other lock
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
    DistributedLockBuilder<DistributedLock> createOverridingLock();

    /**
     * Creates an overriding lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the overriding lock
     * @see Sherlock#createOverridingLock()
     */
    default DistributedLock createOverridingLock(String lockId) {
        return createOverridingLock().withLockId(lockId).build();
    }

    /**
     * Force releases all acquired locks.
     * <p>
     * It could be used for administrative actions.
     *
     * @return true if any lock was released
     */
    boolean forceReleaseAllLocks();

    /**
     * Force releases a lock.
     * <p>
     * It could be used for administrative actions.
     *
     * @param lockId lock identifier
     * @return true if lock was released
     */
    default boolean forceReleaseLock(String lockId) {
        return createOverridingLock(lockId).release();
    }
}
