package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.reactive.ReactiveDistributedLock;
import com.coditory.sherlock.reactive.connector.LockResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import io.reactivex.Single;
import org.reactivestreams.FlowAdapters;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

final class RxJavaDistributedLockWrapper implements RxJavaDistributedLock {
  static RxJavaDistributedLock rxJavaLock(ReactiveDistributedLock lock) {
    return new RxJavaDistributedLockWrapper(lock);
  }

  private final LockResultLogger logger;
  private final ReactiveDistributedLock lock;

  private RxJavaDistributedLockWrapper(ReactiveDistributedLock lock) {
    this.lock = lock;
    this.logger = new LockResultLogger(lock.getId(), lock.getClass());
  }

  @Override
  public String getId() {
    return lock.getId();
  }

  @Override
  public Single<LockResult> acquire() {
    return toSingle(lock.acquire())
        .doOnSuccess(logger::logResult);
  }

  @Override
  public Single<LockResult> acquire(Duration duration) {
    return toSingle(lock.acquire(duration))
        .doOnSuccess(logger::logResult);
  }

  @Override
  public Single<LockResult> acquireForever() {
    return toSingle(lock.acquireForever())
        .doOnSuccess(logger::logResult);
  }

  @Override
  public Single<ReleaseResult> release() {
    return toSingle(lock.release())
        .doOnSuccess(logger::logResult);
  }

  private <T> Single<T> toSingle(Publisher<T> publisher) {
    return Single.fromPublisher(FlowAdapters.toPublisher(publisher));
  }
}
