package com.coditory.sherlock.reactive;

import com.coditory.sherlock.reactive.connector.InitializationResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;

import java.util.concurrent.Flow.Publisher;

/**
 * Creates and manages reactive distributed locks.
 */
public interface ReactiveSherlock {
  Publisher<InitializationResult> initialize();

  ReactiveDistributedLockBuilder createLock();

  default ReactiveDistributedLock createLock(String lockId) {
    return createLock()
      .withLockId(lockId)
      .build();
  }

  ReactiveDistributedLockBuilder createReentrantLock();

  default ReactiveDistributedLock createReentrantLock(String lockId) {
    return createReentrantLock()
      .withLockId(lockId)
      .build();
  }

  ReactiveDistributedLockBuilder createOverridingLock();

  default ReactiveDistributedLock createOverridingLock(String lockId) {
    return createOverridingLock()
      .withLockId(lockId)
      .build();
  }

  Publisher<ReleaseResult> forceReleaseAllLocks();

  default Publisher<ReleaseResult> forceReleaseLock(String lockId) {
    return createOverridingLock(lockId).release();
  }
}
