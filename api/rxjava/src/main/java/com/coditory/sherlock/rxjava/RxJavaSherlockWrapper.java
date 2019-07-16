package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.reactive.ReactiveSherlock;
import com.coditory.sherlock.reactive.connector.InitializationResult;
import io.reactivex.Single;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;
import static com.coditory.sherlock.rxjava.PublisherToSingleConverter.convertToSingle;

final class RxJavaSherlockWrapper implements RxJavaSherlock {
  private final ReactiveSherlock sherlock;

  RxJavaSherlockWrapper(ReactiveSherlock sherlock) {
    this.sherlock = expectNonNull(sherlock, "Expected non null sherlock");
  }

  @Override
  public Single<InitializationResult> initialize() {
    return convertToSingle(sherlock.initialize());
  }

  @Override
  public RxJavaDistributedLock createReentrantLock(String lockId) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createReentrantLock(lockId));
  }

  @Override
  public RxJavaDistributedLock createReentrantLock(String lockId, Duration duration) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createReentrantLock(lockId, duration));
  }

  @Override
  public RxJavaDistributedLock createLock(String lockId) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createLock(lockId));
  }

  @Override
  public RxJavaDistributedLock createLock(String lockId, Duration duration) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createLock(lockId, duration));
  }

  @Override
  public RxJavaDistributedLock createOverridingLock(String lockId) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createOverridingLock(lockId));
  }

  @Override
  public RxJavaDistributedLock createOverridingLock(String lockId, Duration duration) {
    return RxJavaDistributedLockWrapper.rxJavaLock(sherlock.createOverridingLock(lockId, duration));
  }
}
