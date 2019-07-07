package com.coditory.distributed.lock.tests.base

import com.coditory.distributed.lock.DistributedLockDriver
import com.coditory.distributed.lock.common.InstanceId
import com.coditory.distributed.lock.common.LockId
import com.coditory.distributed.lock.common.LockRequest
import com.coditory.distributed.lock.reactive.ReactiveDistributedLockDriver

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
  boolean release(LockId lockId, InstanceId instanceId) {
    return flowPublisherToFlux(reactiveDriver.release(lockId, instanceId))
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
