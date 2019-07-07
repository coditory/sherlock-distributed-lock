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
  boolean lock(LockRequest lockRequest) {
    return flowPublisherToFlux(reactiveDriver.lock(lockRequest))
        .single().block().isLocked()
  }

  @Override
  boolean lockOrRelock(LockRequest lockRequest) {
    return flowPublisherToFlux(reactiveDriver.lockOrRelock(lockRequest))
        .single().block().isLocked()
  }

  @Override
  boolean forceLock(LockRequest lockRequest) {
    return flowPublisherToFlux(reactiveDriver.forceLock(lockRequest))
        .single().block().isLocked()
  }

  @Override
  boolean unlock(LockId lockId, InstanceId instanceId) {
    return flowPublisherToFlux(reactiveDriver.unlock(lockId, instanceId))
        .single().block().isUnlocked()
  }

  @Override
  boolean forceUnlock(LockId lockId) {
    return flowPublisherToFlux(reactiveDriver.forceUnlock(lockId))
        .single().block().isUnlocked()
  }

  @Override
  void forceUnlockAll() {
    flowPublisherToFlux(reactiveDriver.forceUnlockAll())
        .single().block()
  }
}
