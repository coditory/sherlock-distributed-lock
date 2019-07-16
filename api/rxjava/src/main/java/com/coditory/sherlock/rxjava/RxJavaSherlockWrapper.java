package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.reactive.ReactiveSherlock;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

final class RxJavaSherlockWrapper implements RxJavaSherlock {
  private final ReactiveSherlock sherlock;

  RxJavaSherlockWrapper(ReactiveSherlock sherlock) {
    this.sherlock = expectNonNull(sherlock, "Expected non null sherlock");
  }

  public RxJavaDistributedLock createReentrantLock(String lockId) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createReentrantLock(lockId));
  }

  public RxJavaDistributedLock createReentrantLock(String lockId, Duration duration) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createReentrantLock(lockId, duration));
  }

  public RxJavaDistributedLock createLock(String lockId) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createLock(lockId));
  }

  public RxJavaDistributedLock createLock(String lockId, Duration duration) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createLock(lockId, duration));
  }

  public RxJavaDistributedLock createOverridingLock(String lockId) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createOverridingLock(lockId));
  }

  public RxJavaDistributedLock createOverridingLock(String lockId, Duration duration) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createOverridingLock(lockId, duration));
  }
}
