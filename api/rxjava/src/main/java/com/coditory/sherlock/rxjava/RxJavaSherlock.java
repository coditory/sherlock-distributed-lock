package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.reactive.ReactiveSherlock;
import com.coditory.sherlock.reactive.connector.InitializationResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import io.reactivex.Single;

/**
 * Creates and manages reactive distributed locks with reactor api.
 */
public interface RxJavaSherlock {
  /**
   * Maps reactive sherlock to a one using RxJava API
   *
   * @param locks reactive locks to be wrapped in Reactor api
   * @return reactor version of sherlock locks
   */
  static RxJavaSherlock toRxSherlock(ReactiveSherlock locks) {
    return new RxJavaSherlockWrapper(locks);
  }

  Single<InitializationResult> initialize();

  RxJavaDistributedLockBuilder createLock();

  default RxJavaDistributedLock createLock(String lockId) {
    return createLock()
      .withLockId(lockId)
      .build();
  }

  RxJavaDistributedLockBuilder createReentrantLock();

  default RxJavaDistributedLock createReentrantLock(String lockId) {
    return createReentrantLock()
      .withLockId(lockId)
      .build();
  }

  RxJavaDistributedLockBuilder createOverridingLock();

  default RxJavaDistributedLock createOverridingLock(String lockId) {
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
