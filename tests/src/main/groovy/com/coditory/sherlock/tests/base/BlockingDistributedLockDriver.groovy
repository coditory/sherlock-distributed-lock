package com.coditory.sherlock.tests.base

import com.coditory.sherlock.DistributedLockDriver
import com.coditory.sherlock.common.LockId
import com.coditory.sherlock.common.LockRequest
import com.coditory.sherlock.common.OwnerId
import com.coditory.sherlock.reactive.ReactiveDistributedLockDriver

import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux

class BlockingDistributedLockDriver implements DistributedLockDriver {
  static DistributedLockDriver toBlockingDriver(ReactiveDistributedLockDriver reactiveDriver) {
    return new BlockingDistributedLockDriver(reactiveDriver)
  }

  private final ReactiveDistributedLockDriver reactiveDriver

  BlockingDistributedLockDriver(ReactiveDistributedLockDriver reactiveDriver) {
    this.reactiveDriver = reactiveDriver
  }

  @Override
  void initialize() {
    flowPublisherToFlux(reactiveDriver.initialize())
        .blockLast()
  }

  @Override
  boolean acquire(LockRequest lockRequest) {
    return flowPublisherToFlux(reactiveDriver.acquire(lockRequest))
        .single().block().isLocked()
  }

  @Override
  boolean acquireOrProlong(LockRequest lockRequest) {
    return flowPublisherToFlux(reactiveDriver.acquireOrProlong(lockRequest))
        .single().block().isLocked()
  }

  @Override
  boolean forceAcquire(LockRequest lockRequest) {
    return flowPublisherToFlux(reactiveDriver.forceAcquire(lockRequest))
        .single().block().isLocked()
  }

  @Override
  boolean release(LockId lockId, OwnerId ownerId) {
    return flowPublisherToFlux(reactiveDriver.release(lockId, ownerId))
        .single().block().isUnlocked()
  }

  @Override
  boolean forceRelease(LockId lockId) {
    return flowPublisherToFlux(reactiveDriver.forceRelease(lockId))
        .single().block().isUnlocked()
  }

  @Override
  void forceReleaseAll() {
    flowPublisherToFlux(reactiveDriver.forceReleaseAll())
        .single().block()
  }
}
