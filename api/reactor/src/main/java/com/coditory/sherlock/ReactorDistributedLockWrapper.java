package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.coditory.sherlock.PublisherToMonoConverter.convertToMono;

final class ReactorDistributedLockWrapper implements ReactorDistributedLock {
  private final LockResultLogger logger;
  private final ReactiveDistributedLock lock;

  ReactorDistributedLockWrapper(ReactiveDistributedLock lock) {
    this.lock = lock;
    this.logger = new LockResultLogger(lock.getId(), lock.getClass());
  }

  @Override
  public String getId() {
    return lock.getId();
  }

  @Override
  public Mono<AcquireResult> acquire() {
    return convertToMono(lock.acquire())
      .doOnNext(logger::logResult);
  }

  @Override
  public Mono<AcquireResult> acquire(Duration duration) {
    return convertToMono(lock.acquire(duration))
      .doOnNext(logger::logResult);
  }

  @Override
  public Mono<AcquireResult> acquireForever() {
    return convertToMono(lock.acquireForever())
      .doOnNext(logger::logResult);
  }

  @Override
  public Mono<ReleaseResult> release() {
    return convertToMono(lock.release())
      .doOnNext(logger::logResult);
  }
}
