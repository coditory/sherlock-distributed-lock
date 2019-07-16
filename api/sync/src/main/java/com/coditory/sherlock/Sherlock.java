package com.coditory.sherlock;

import java.time.Duration;

/**
 * Creates and manages distributed locks.
 */
public interface Sherlock {
  /**
   * Create a distributed lock. Lock expires after configured lock duration.
   *
   * @param lockId the lock id
   * @return the lock
   * @see Sherlock#createLock(String, Duration)
   */
  DistributedLock createLock(String lockId);

  /**
   * Create a lock. Created lock may be acquired only once by the same application instance:
   *
   * <pre>{@code
   * assert reentrantLock.acquire() == true
   * assert reentrantLock.acquire() == false
   * }</pre>
   *
   * @param lockId the lock id
   * @param duration after that time lock expires and is released
   * @return the lock
   */
  DistributedLock createLock(String lockId, Duration duration);

  /**
   * Create a distributed reentrant lock. Lock expires after configured lock duration.
   *
   * @param lockId the lock id
   * @return the reentrant lock
   * @see Sherlock#createReentrantLock(String, Duration)
   */
  DistributedLock createReentrantLock(String lockId);

  /**
   * Create a distributed reentrant lock. Reentrant lock maybe acquired multiple times by the same
   * application instance:
   *
   * <pre>{@code
   * assert reentrantLock.acquire() == true
   * assert reentrantLock.acquire() == true
   * }</pre>
   *
   * @param lockId the lock id
   * @param duration after that time lock expires and is released
   * @return the reentrant lock
   */
  DistributedLock createReentrantLock(String lockId, Duration duration);

  /**
   * Create a distributed overriding lock. Lock expires after configured lock duration.
   *
   * @param lockId the lock id
   * @return the reentrant lock
   * @see Sherlock#createOverridingLock(String, Duration)
   */
  DistributedLock createOverridingLock(String lockId);

  /**
   * Create a distributed overriding lock. Returned lock overrides lock state without checking if it
   * was released.
   *
   * @param lockId the lock id
   * @param duration after that time lock expires and is released
   * @return the reentrant lock
   */
  DistributedLock createOverridingLock(String lockId, Duration duration);
}
