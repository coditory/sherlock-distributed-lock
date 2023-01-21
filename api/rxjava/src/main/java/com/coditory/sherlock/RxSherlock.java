package com.coditory.sherlock;

import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;

/**
 * Manages distributed locks using RxJava API.
 */
public interface RxSherlock {
    /**
     * Initializes underlying infrastructure. If this method is not invoked explicitly then it can be
     * invoked implicitly when acquiring or releasing a lock for the first time.
     * <p>
     * Most often initialization is related with creating indexes and tables.
     *
     * @return {@link InitializationResult#SUCCESS} if initialization was successful, otherwise {@link
     * InitializationResult#FAILURE} is returned
     */
    @NotNull
    Single<InitializationResult> initialize();

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
    @NotNull
    DistributedLockBuilder<RxDistributedLock> createLock();

    /**
     * Creates a lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the lock
     * @see RxSherlock#createLock()
     */
    @NotNull
    default RxDistributedLock createLock(@NotNull String lockId) {
        expectNonEmpty(lockId, "lockId");
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
    @NotNull
    DistributedLockBuilder<RxDistributedLock> createReentrantLock();

    /**
     * Creates a reentrant lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the reentrant lock
     * @see RxSherlock#createReentrantLock()
     */
    @NotNull
    default RxDistributedLock createReentrantLock(@NotNull String lockId) {
        expectNonEmpty(lockId, "lockId");
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
    @NotNull
    DistributedLockBuilder<RxDistributedLock> createOverridingLock();

    /**
     * Creates an overriding lock with default configuration.
     *
     * @param lockId lock identifier
     * @return the overriding lock
     * @see RxSherlock#createOverridingLock()
     */
    @NotNull
    default RxDistributedLock createOverridingLock(@NotNull String lockId) {
        expectNonEmpty(lockId, "lockId");
        return createOverridingLock().withLockId(lockId).build();
    }

    /**
     * Force releases all acquired locks.
     * <p>
     * It could be used for administrative actions.
     *
     * @return {@link ReleaseResult#SUCCESS} if lock was released, otherwise {@link
     * ReleaseResult#FAILURE} is returned
     */
    @NotNull
    Single<ReleaseResult> forceReleaseAllLocks();

    /**
     * Force releases a lock.
     * <p>
     * It could be used for administrative actions.
     *
     * @param lockId lock identifier
     * @return {@link ReleaseResult#SUCCESS} if lock was released, otherwise {@link
     * ReleaseResult#FAILURE} is returned
     */
    @NotNull
    default Single<ReleaseResult> forceReleaseLock(@NotNull String lockId) {
        expectNonEmpty(lockId, "lockId");
        return createOverridingLock(lockId).release();
    }
}
