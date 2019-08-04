package com.coditory.sherlock.reactor;

import com.coditory.sherlock.reactive.ReactiveDistributedLock;
import com.coditory.sherlock.reactive.connector.AcquireResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux;

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
    return flowPublisherToFlux(lock.acquire())
        .single()
        .doOnNext(logger::logResult);
  }

  @Override
  public Mono<AcquireResult> acquire(Duration duration) {
    return flowPublisherToFlux(lock.acquire(duration))
        .single()
        .doOnNext(logger::logResult);
  }

  @Override
  public Mono<AcquireResult> acquireForever() {
    return flowPublisherToFlux(lock.acquireForever())
        .single()
        .doOnNext(logger::logResult);
  }

  @Override
  public Mono<ReleaseResult> release() {
    return flowPublisherToFlux(lock.release())
        .single()
        .doOnNext(logger::logResult);
  }
}
