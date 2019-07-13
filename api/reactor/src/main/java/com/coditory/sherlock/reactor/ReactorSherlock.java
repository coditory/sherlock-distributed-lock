package com.coditory.sherlock.reactor;

import com.coditory.sherlock.reactive.ReactiveSherlock;

import java.time.Duration;

public interface ReactorSherlock {
  /**
   * Maps reactive sherlock to a one using Reactor's {@link reactor.core.publisher.Mono} and {@link
   * reactor.core.publisher.Flux}
   */
  static ReactorSherlock reactorSherlock(ReactiveSherlock locks) {
    return new ReactorSherlockWithDriver(locks);
  }

  /**
   * @return owner id is most often the application instance id
   */
  String getOwnerId();

  /**
   * @return the default lock duration
   */
  Duration getLockDuration();

  /**
   * Create distributed lock. Lock expires after {@link ReactorSherlock#getLockDuration()}.
   *
   * @param lockId - the lock id
   * @return the lock
   * @see ReactorSherlock#createLock(String, Duration)
   */
  ReactorDistributedLock createLock(String lockId);

  /**
   * Create lock with the lock id and specific lock duration. Created lock may be acquired only once
   * by the same application instance:
   *
   * <pre>{@code
   * assert reentrantLock.acquire() == true
   * assert reentrantLock.acquire() == false
   * }</pre>
   *
   * @param lockId - the lock id
   * @param duration - after that time lock expires and is released
   * @return the lock
   */
  ReactorDistributedLock createLock(String lockId, Duration duration);

  /**
   * Create distributed reentrant lock. Lock expires after {@link ReactorSherlock#getLockDuration()}.
   *
   * @param lockId - the lock id
   * @return the reentrant lock
   * @see ReactorSherlock#createReentrantLock(String, Duration)
   */
  ReactorDistributedLock createReentrantLock(String lockId);

  /**
   * Create distributed reentrant lock with the lock id and specific lock duration. Reentrant lock
   * maybe acquired multiple times by the same application instance:
   *
   * <pre>{@code
   * assert reentrantLock.acquire() == true
   * assert reentrantLock.acquire() == true
   * }</pre>
   *
   * @param lockId - the lock id
   * @param duration - after that time lock expires and is released
   * @return the reentrant lock
   */
  ReactorDistributedLock createReentrantLock(String lockId, Duration duration);

  /**
   * Create a distributed overriding lock. Lock expires after {@link ReactorSherlock#getLockDuration()}.
   *
   * @param lockId - the lock id
   * @return the reentrant lock
   * @see ReactorSherlock#createOverridingLock(String, Duration)
   */
  ReactorDistributedLock createOverridingLock(String lockId);

  /**
   * Create a distributed overriding lock. Returned lock overrides lock state without checking if it
   * was released.
   *
   * @param lockId - the lock id
   * @param duration - after that time lock expires and is released
   * @return the reentrant lock
   */
  ReactorDistributedLock createOverridingLock(String lockId, Duration duration);
}
