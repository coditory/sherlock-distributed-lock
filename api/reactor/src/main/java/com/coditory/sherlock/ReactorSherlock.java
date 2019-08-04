package com.coditory.sherlock;

import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Mono;

/**
 * Creates and manages reactive distributed locks with reactor api.
 */
public interface ReactorSherlock {
  /**
   * Maps reactive sherlock to a one using Reactor's {@link reactor.core.publisher.Mono} and {@link
   * reactor.core.publisher.Flux}
   *
   * @param locks reactive locks to be wrapped in Reactor api
   * @return reactor version of sherlock locks
   */
  static ReactorSherlock toReactorSherlock(ReactiveSherlock locks) {
    return new ReactorSherlockWrapper(locks);
  }

  Mono<InitializationResult> initialize();

  DistributedLockBuilder<ReactorDistributedLock> createLock();

  default ReactorDistributedLock createLock(String lockId) {
    return createLock().withLockId(lockId).build();
  }

  DistributedLockBuilder<ReactorDistributedLock> createReentrantLock();

  default ReactorDistributedLock createReentrantLock(String lockId) {
    return createReentrantLock().withLockId(lockId).build();
  }

  DistributedLockBuilder<ReactorDistributedLock> createOverridingLock();

  default ReactorDistributedLock createOverridingLock(String lockId) {
    return createOverridingLock().withLockId(lockId).build();
  }

  Mono<ReleaseResult> forceReleaseAllLocks();

  default Mono<ReleaseResult> forceReleaseLock(String lockId) {
    return createOverridingLock(lockId).release();
  }
}
