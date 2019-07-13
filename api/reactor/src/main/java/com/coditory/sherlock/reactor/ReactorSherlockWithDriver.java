package com.coditory.sherlock.reactor;

import com.coditory.sherlock.reactive.ReactiveSherlock;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class ReactorSherlockWithDriver implements ReactorSherlock {
  private final ReactiveSherlock sherlock;

  ReactorSherlockWithDriver(ReactiveSherlock sherlock) {
    this.sherlock = expectNonNull(sherlock, "Expected non null sherlock");
  }

  public String getOwnerId() {
    return sherlock.getOwnerId();
  }

  public Duration getLockDuration() {
    return sherlock.getLockDuration();
  }

  public ReactorDistributedLock createReentrantLock(String lockId) {
    return ReactorDistributedLockWrapper.reactorLock(sherlock.createReentrantLock(lockId));
  }

  public ReactorDistributedLock createReentrantLock(String lockId, Duration duration) {
    return ReactorDistributedLockWrapper.reactorLock(sherlock.createReentrantLock(lockId, duration));
  }

  public ReactorDistributedLock createLock(String lockId) {
    return ReactorDistributedLockWrapper.reactorLock(sherlock.createLock(lockId));
  }

  public ReactorDistributedLock createLock(String lockId, Duration duration) {
    return ReactorDistributedLockWrapper.reactorLock(sherlock.createLock(lockId, duration));
  }

  public ReactorDistributedLock createOverridingLock(String lockId) {
    return ReactorDistributedLockWrapper.reactorLock(sherlock.createOverridingLock(lockId));
  }

  public ReactorDistributedLock createOverridingLock(String lockId, Duration duration) {
    return ReactorDistributedLockWrapper.reactorLock(sherlock.createOverridingLock(lockId, duration));
  }
}
