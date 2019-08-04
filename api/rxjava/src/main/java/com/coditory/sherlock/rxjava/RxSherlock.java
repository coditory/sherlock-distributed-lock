package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.reactive.ReactiveSherlock;
import com.coditory.sherlock.reactive.connector.InitializationResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import io.reactivex.Single;

/**
 * Creates and manages reactive distributed locks with reactor api.
 */
public interface RxSherlock {
  /**
   * Maps reactive sherlock to a one using RxJava API
   *
   * @param locks reactive locks to be wrapped in Reactor api
   * @return reactor version of sherlock locks
   */
  static RxSherlock toRxSherlock(ReactiveSherlock locks) {
    return new RxSherlockWrapper(locks);
  }

  Single<InitializationResult> initialize();

  RxDistributedLockBuilder createLock();

  default RxDistributedLock createLock(String lockId) {
    return createLock()
      .withLockId(lockId)
      .build();
  }

  RxDistributedLockBuilder createReentrantLock();

  default RxDistributedLock createReentrantLock(String lockId) {
    return createReentrantLock()
      .withLockId(lockId)
      .build();
  }

  RxDistributedLockBuilder createOverridingLock();

  default RxDistributedLock createOverridingLock(String lockId) {
    return createOverridingLock()
      .withLockId(lockId)
      .build();
  }

  Single<ReleaseResult> forceReleaseAllLocks();

  default Single<ReleaseResult> forceReleaseLock(String lockId) {
    return createOverridingLock(lockId)
      .release();
  }
}
