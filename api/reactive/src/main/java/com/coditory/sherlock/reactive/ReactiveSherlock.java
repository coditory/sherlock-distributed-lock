package com.coditory.sherlock.reactive;

import com.coditory.sherlock.reactive.connector.InitializationResult;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;
import java.util.function.Function;

/**
 * Creates and manages reactive distributed locks.
 */
public interface ReactiveSherlock {
  /**
   * Initializes underlying infrastructure. If it's database then indexes and tables are created. If
   * this method is not invoked directly then it is invoked implicitly when acquiring or releasing a
   * lock for the first time.
   */
  Publisher<InitializationResult> initialize();

  /**
   * Create a distributed lock. Lock expires after configured lock duration.
   *
   * @param lockId the lock id
   * @return the lock
   * @see ReactiveSherlock#createLock(String, Duration)
   */
  ReactiveDistributedLock createLock(String lockId);

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
  ReactiveDistributedLock createLock(String lockId, Duration duration);

  /**
   * Create a distributed reentrant lock. Lock expires after configured lock duration.
   *
   * @param lockId the lock id
   * @return the reentrant lock
   * @see ReactiveSherlock#createReentrantLock(String, Duration)
   */
  ReactiveDistributedLock createReentrantLock(String lockId);

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
  ReactiveDistributedLock createReentrantLock(String lockId, Duration duration);

  /**
   * Create a distributed overriding lock. Lock expires after configured lock duration.
   *
   * @param lockId the lock id
   * @return the reentrant lock
   * @see ReactiveSherlock#createOverridingLock(String, Duration)
   */
  ReactiveDistributedLock createOverridingLock(String lockId);

  /**
   * Create a distributed overriding lock. Returned lock overrides lock state without checking if it
   * was released.
   *
   * @param lockId the lock id
   * @param duration after that time lock expires and is released
   * @return the reentrant lock
   */
  ReactiveDistributedLock createOverridingLock(String lockId, Duration duration);

  default <T> T map(Function<ReactiveSherlock, T> mapper) {
    return mapper.apply(this);
  }
}
