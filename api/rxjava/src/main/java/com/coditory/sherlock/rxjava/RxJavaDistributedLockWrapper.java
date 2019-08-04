package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.reactive.ReactiveDistributedLock;
import com.coditory.sherlock.reactive.connector.AcquireResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import io.reactivex.Single;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

import static com.coditory.sherlock.rxjava.PublisherToSingleConverter.convertToSingle;

final class RxJavaDistributedLockWrapper implements RxJavaDistributedLock {
  private final LockResultLogger logger;
  private final ReactiveDistributedLock lock;

  RxJavaDistributedLockWrapper(ReactiveDistributedLock lock) {
    this.lock = lock;
    this.logger = new LockResultLogger(lock.getId(), lock.getClass());
  }

  @Override
  public String getId() {
    return lock.getId();
  }

  @Override
  public Single<AcquireResult> acquire() {
    return toSingle(lock.acquire())
        .doOnSuccess(logger::logResult);
  }

  @Override
  public Single<AcquireResult> acquire(Duration duration) {
    return toSingle(lock.acquire(duration))
        .doOnSuccess(logger::logResult);
  }

  @Override
  public Single<AcquireResult> acquireForever() {
    return toSingle(lock.acquireForever())
        .doOnSuccess(logger::logResult);
  }

  @Override
  public Single<ReleaseResult> release() {
    return toSingle(lock.release())
        .doOnSuccess(logger::logResult);
  }

  private <T> Single<T> toSingle(Publisher<T> publisher) {
    return convertToSingle(publisher);
  }
}
