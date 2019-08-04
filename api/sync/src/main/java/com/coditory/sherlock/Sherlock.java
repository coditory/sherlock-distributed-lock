package com.coditory.sherlock;

/**
 * Creates and manages distributed locks.
 */
public interface Sherlock {

  /**
   * Initializes underlying infrastructure. If this method is not invoked directly then it can be
   * invoked implicitly when acquiring or releasing a lock for the first time.
   * <p>
   * Most often initialization is related with creating indexes and tables.
   */
  void initialize();

  /**
   * Create a lock. Created lock may be acquired only once by the same application instance:
   *
   * <pre>{@code
   * assert lock.acquire() == true
   * assert lock.acquire() == false
   * }</pre>
   *
   * @return the lock
   */
  DistributedLockBuilder<DistributedLock> createLock();

  default DistributedLock createLock(String lockId) {
    return createLock().withLockId(lockId).build();
  }

  /**
   * Create a distributed reentrant lock. Reentrant lock maybe acquired multiple times by the same
   * application instance:
   *
   * <pre>{@code
   * assert reentrantLock.acquire() == true
   * assert reentrantLock.acquire() == true
   * }</pre>
   *
   * @return the reentrant lock
   */
  DistributedLockBuilder<DistributedLock> createReentrantLock();

  default DistributedLock createReentrantLock(String lockId) {
    return createReentrantLock().withLockId(lockId).build();
  }

  /**
   * Create a distributed overriding lock. Returned lock overrides lock state without checking if it
   * was released.
   *
   * @return the reentrant lock
   */
  DistributedLockBuilder<DistributedLock> createOverridingLock();

  /**
   * @param lockId lock identifier
   * @see Sherlock#createOverridingLock()
   */
  default DistributedLock createOverridingLock(String lockId) {
    return createOverridingLock().withLockId(lockId).build();
  }

  boolean forceReleaseAllLocks();

  default boolean forceReleaseLock(String lockId) {
    return createOverridingLock(lockId).release();
  }
}
