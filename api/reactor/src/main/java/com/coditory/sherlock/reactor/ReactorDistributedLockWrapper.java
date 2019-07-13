package com.coditory.sherlock.reactor;

import com.coditory.sherlock.reactive.ReactiveDistributedLock;
import com.coditory.sherlock.reactive.connector.LockResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux;

final class ReactorDistributedLockWrapper implements ReactorDistributedLock {
  static ReactorDistributedLock reactorLock(ReactiveDistributedLock lock) {
    return new ReactorDistributedLockWrapper(lock);
  }

  private final LockResultLogger logger;
  private final ReactiveDistributedLock lock;

  private ReactorDistributedLockWrapper(ReactiveDistributedLock lock) {
    this.lock = lock;
    this.logger = new LockResultLogger(lock.getId(), lock.getClass());
  }

  @Override
  public String getId() {
    return lock.getId();
  }

  @Override
  public Mono<LockResult> acquire() {
    return flowPublisherToFlux(lock.acquire())
        .single()
        .doOnNext(logger::logResult);
  }

  @Override
  public Mono<LockResult> acquire(Duration duration) {
    return flowPublisherToFlux(lock.acquire(duration))
        .single()
        .doOnNext(logger::logResult);
  }

  @Override
  public Mono<LockResult> acquireForever() {
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
