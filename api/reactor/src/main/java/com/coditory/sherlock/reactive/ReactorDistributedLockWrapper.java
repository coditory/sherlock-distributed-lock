package com.coditory.sherlock.reactive;

import com.coditory.sherlock.reactive.driver.LockResult;
import com.coditory.sherlock.reactive.driver.UnlockResult;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux;

final class ReactorDistributedLockWrapper implements ReactorDistributedLock {
  static ReactorDistributedLock reactorLock(ReactiveDistributedLock lock) {
    return new ReactorDistributedLockWrapper(lock);
  }

  private final ReactiveDistributedLock lock;

  private ReactorDistributedLockWrapper(ReactiveDistributedLock lock) {
    this.lock = lock;
  }

  @Override
  public String getId() {
    return lock.getId();
  }

  @Override
  public Mono<LockResult> acquire() {
    return flowPublisherToFlux(lock.acquire()).single();
  }

  @Override
  public Mono<LockResult> acquire(Duration duration) {
    return flowPublisherToFlux(lock.acquire(duration)).single();
  }

  @Override
  public Mono<LockResult> acquireForever() {
    return flowPublisherToFlux(lock.acquireForever()).single();
  }

  @Override
  public Mono<UnlockResult> release() {
    return flowPublisherToFlux(lock.release()).single();
  }
}
