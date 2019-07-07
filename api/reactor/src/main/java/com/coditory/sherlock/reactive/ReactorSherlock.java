package com.coditory.sherlock.reactive;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;
import static com.coditory.sherlock.reactive.ReactorDistributedLock.reactorLock;

public final class ReactorSherlock {
  public static ReactorSherlock reactorSherlock(ReactiveSherlock locks) {
    return new ReactorSherlock(locks);
  }

  private final ReactiveSherlock locks;

  private ReactorSherlock(ReactiveSherlock locks) {
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
