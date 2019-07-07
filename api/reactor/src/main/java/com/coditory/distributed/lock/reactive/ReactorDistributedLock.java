package com.coditory.distributed.lock.reactive;

import com.coditory.distributed.lock.reactive.driver.LockResult;
import com.coditory.distributed.lock.reactive.driver.UnlockResult;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux;

public final class ReactorDistributedLock {
  static ReactorDistributedLock reactorLock(ReactiveDistributedLock lock) {
    return new ReactorDistributedLock(lock);
  }

  private final ReactiveDistributedLock lock;

  private ReactorDistributedLock(ReactiveDistributedLock lock) {
    this.lock = lock;
  }

  public String getId() {
    return lock.getId();
  }

  public Mono<LockResult> acquire() {
    return flowPublisherToFlux(lock.acquire()).single();
  }

  public Mono<LockResult> acquire(Duration duration) {
    return flowPublisherToFlux(lock.acquire(duration)).single();
  }

  public Mono<LockResult> acquireForever() {
    return flowPublisherToFlux(lock.acquireForever()).single();
  }

  public Mono<UnlockResult> release() {
    return flowPublisherToFlux(lock.release()).single();
  }
}
