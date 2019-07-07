package com.coditory.distributed.lock.tests.base

import com.coditory.distributed.lock.DistributedLock
import com.coditory.distributed.lock.reactive.ReactiveDistributedLock

import java.time.Duration

import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux

class BlockingReactiveDistributedLock implements DistributedLock {
  static blockingLock(ReactiveDistributedLock lock) {
    return new BlockingReactiveDistributedLock(lock)
  }

  private final ReactiveDistributedLock lock

  private BlockingReactiveDistributedLock(ReactiveDistributedLock lock) {
    this.lock = lock
  }

  @Override
  String getId() {
    return lock.id
  }

  @Override
  boolean acquire() {
    return flowPublisherToFlux(lock.acquire())
        .single().block().locked
  }

  @Override
  boolean acquire(Duration duration) {
    return flowPublisherToFlux(lock.acquire(duration))
        .single().block().locked
  }

  @Override
  boolean acquireForever() {
    return flowPublisherToFlux(lock.acquireForever())
        .single().block().locked
  }

  @Override
  boolean release() {
    return flowPublisherToFlux(lock.release())
        .single().block().unlocked
  }
}
