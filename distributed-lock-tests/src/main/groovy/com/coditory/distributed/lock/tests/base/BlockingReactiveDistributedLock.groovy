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
  boolean lock() {
    return flowPublisherToFlux(lock.lock())
        .single().block().locked
  }

  @Override
  boolean lock(Duration duration) {
    return flowPublisherToFlux(lock.lock(duration))
        .single().block().locked
  }

  @Override
  boolean lockInfinitely() {
    return flowPublisherToFlux(lock.lockInfinitely())
        .single().block().locked
  }

  @Override
  boolean unlock() {
    return flowPublisherToFlux(lock.unlock())
        .single().block().unlocked
  }
}
