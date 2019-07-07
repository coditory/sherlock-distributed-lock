package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.InstanceId;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;
import static com.coditory.sherlock.reactive.ReactorDistributedLock.reactorLock;

public final class ReactorSherlock {
  public static ReactorSherlock reactorSherlock(ReactiveSherlock locks) {
    return new ReactorSherlock(locks);
  }

  private final ReactiveSherlock sherlock;

  private ReactorSherlock(ReactiveSherlock sherlock) {
    this.sherlock = expectNonNull(sherlock, "Expected non null sherlock");
  }

  public InstanceId getInstanceId() {
    return sherlock.getInstanceId();
  }

  public Duration getLockDuration() {
    return sherlock.getDuration();
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
