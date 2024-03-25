package com.coditory.sherlock;

import com.coditory.sherlock.connector.ReleaseResult;
import org.jetbrains.annotations.NotNull;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;

/**
 * Manages distributed locks.
 */
public interface Sherlock {

    /**
     * Initializes underlying infrastructure. If this method is not invoked explicitly then it can be
     * invoked implicitly when acquiring or releasing a lock for the first time.
     * <p>
     * Initialization creates indexes and tables.
     */
    void initialize();

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
    @NotNull
    DistributedLockBuilder<DistributedLock> createLock();

    /**
     * Creates a single-entrant lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the lock
     * @see Sherlock#createLock()
     */
    @NotNull
    default DistributedLock createLock(@NotNull String lockId) {
        return createLock().withLockId(lockId).build();
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
    @NotNull
    DistributedLockBuilder<DistributedLock> createReentrantLock();

    /**
     * Creates a reentrant lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the reentrant lock
     * @see Sherlock#createReentrantLock()
     */
    @NotNull
    default DistributedLock createReentrantLock(@NotNull String lockId) {
        expectNonEmpty(lockId, "lockId");
        return createReentrantLock().withLockId(lockId).build();
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
    @NotNull
    DistributedLockBuilder<DistributedLock> createOverridingLock();

    /**
     * Creates an overriding lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the overriding lock
     * @see Sherlock#createOverridingLock()
     */
    @NotNull
    default DistributedLock createOverridingLock(@NotNull String lockId) {
        expectNonEmpty(lockId, "lockId");
        return createOverridingLock().withLockId(lockId).build();
    }

    /**
     * Force releases all acquired locks.
     * <p>
     * It could be used for administrative actions.
     *
     * @return {@link ReleaseResult}
     */
    boolean forceReleaseAllLocks();

    /**
     * Force releases a lock.
     * <p>
     * It could be used for administrative actions.
     *
     * @param lockId lock identifier
     * @return {@link ReleaseResult}
     */
    default boolean forceReleaseLock(@NotNull String lockId) {
        expectNonEmpty(lockId, "lockId");
        return createOverridingLock(lockId).release();
    }
}
