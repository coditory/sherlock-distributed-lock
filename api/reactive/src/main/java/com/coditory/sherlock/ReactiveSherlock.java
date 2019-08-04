package com.coditory.sherlock;

import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;

import java.util.concurrent.Flow.Publisher;

/**
 * Creates and manages reactive distributed locks.
 */
public interface ReactiveSherlock {
  Publisher<InitializationResult> initialize();

  DistributedLockBuilder<ReactiveDistributedLock> createLock();

  default ReactiveDistributedLock createLock(String lockId) {
    return createLock()
      .withLockId(lockId)
      .build();
  }

  DistributedLockBuilder<ReactiveDistributedLock> createReentrantLock();

  default ReactiveDistributedLock createReentrantLock(String lockId) {
    return createReentrantLock()
      .withLockId(lockId)
      .build();
  }

  DistributedLockBuilder<ReactiveDistributedLock> createOverridingLock();

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
