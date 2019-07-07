package com.coditory.sherlock.reactive;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;
import static com.coditory.sherlock.reactive.ReactorDistributedLockWrapper.reactorLock;

final class ReactorSherlockWithDriver implements ReactorSherlock {
  private final ReactiveSherlock sherlock;

  ReactorSherlockWithDriver(ReactiveSherlock sherlock) {
    this.sherlock = expectNonNull(sherlock, "Expected non null sherlock");
  }

  public String getInstanceId() {
    return sherlock.getInstanceId();
  }

  public Duration getLockDuration() {
    return sherlock.getLockDuration();
  }

  public ReactorDistributedLock createReentrantLock(String lockId) {
    return reactorLock(sherlock.createReentrantLock(lockId));
  }

  public ReactorDistributedLock createReentrantLock(String lockId, Duration duration) {
    return reactorLock(sherlock.createReentrantLock(lockId, duration));
  }

  public ReactorDistributedLock createLock(String lockId) {
    return reactorLock(sherlock.createLock(lockId));
  }

  public ReactorDistributedLock createLock(String lockId, Duration duration) {
    return reactorLock(sherlock.createLock(lockId, duration));
  }

  public ReactorDistributedLock createOverridingLock(String lockId) {
    return reactorLock(sherlock.createOverridingLock(lockId));
  }

  public ReactorDistributedLock createOverridingLock(String lockId, Duration duration) {
    return reactorLock(sherlock.createOverridingLock(lockId, duration));
  }
}
