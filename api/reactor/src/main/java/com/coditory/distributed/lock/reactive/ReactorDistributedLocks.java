package com.coditory.distributed.lock.reactive;

import java.time.Duration;

import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;
import static com.coditory.distributed.lock.reactive.ReactorDistributedLock.reactorLock;

public final class ReactorDistributedLocks {
  static ReactorDistributedLocks reactorLocks(ReactiveDistributedLocks locks) {
    return new ReactorDistributedLocks(locks);
  }

  private final ReactiveDistributedLocks locks;

  private ReactorDistributedLocks(ReactiveDistributedLocks locks) {
    this.locks = expectNonNull(locks);
  }

  public ReactorDistributedLock createReentrantLock(String lockId) {
    return reactorLock(locks.createReentrantLock(lockId));
  }

  public ReactorDistributedLock createReentrantLock(String lockId, Duration duration) {
    return reactorLock(locks.createReentrantLock(lockId, duration));
  }

  public ReactorDistributedLock createLock(String lockId) {
    return reactorLock(locks.createLock(lockId));
  }

  public ReactorDistributedLock createLock(String lockId, Duration duration) {
    return reactorLock(locks.createLock(lockId, duration));
  }

  public ReactorDistributedLock createOverridingLock(String lockId) {
    return reactorLock(locks.createOverridingLock(lockId));
  }

  public ReactorDistributedLock createOverridingLock(String lockId, Duration duration) {
    return reactorLock(locks.createOverridingLock(lockId, duration));
  }
}
