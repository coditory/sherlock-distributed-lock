package com.coditory.sherlock.tests.base

import com.coditory.sherlock.DistributedLockConnector
import com.coditory.sherlock.common.LockId
import com.coditory.sherlock.common.LockRequest
import com.coditory.sherlock.common.OwnerId
import com.coditory.sherlock.reactive.ReactiveDistributedLockConnector

import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux

class BlockingDistributedLockConnector implements DistributedLockConnector {
  static DistributedLockConnector toBlockingConnector(ReactiveDistributedLockConnector reactiveConnector) {
    return new BlockingDistributedLockConnector(reactiveConnector)
  }

  private final ReactiveDistributedLockConnector reactiveConnector

  BlockingDistributedLockConnector(ReactiveDistributedLockConnector reactiveConnector) {
    this.reactiveConnector = reactiveConnector
  }

  @Override
  void initialize() {
    flowPublisherToFlux(reactiveConnector.initialize())
      .blockLast()
  }

  @Override
  boolean acquire(LockRequest lockRequest) {
    return flowPublisherToFlux(reactiveConnector.acquire(lockRequest))
      .single().block().isAcquired()
  }

  @Override
  boolean acquireOrProlong(LockRequest lockRequest) {
    return flowPublisherToFlux(reactiveConnector.acquireOrProlong(lockRequest))
      .single().block().isAcquired()
  }

  @Override
  boolean forceAcquire(LockRequest lockRequest) {
    return flowPublisherToFlux(reactiveConnector.forceAcquire(lockRequest))
      .single().block().isAcquired()
  }

  @Override
  boolean release(LockId lockId, OwnerId ownerId) {
    return flowPublisherToFlux(reactiveConnector.release(lockId, ownerId))
      .single().block().isReleased()
  }

  @Override
  boolean forceRelease(LockId lockId) {
    return flowPublisherToFlux(reactiveConnector.forceRelease(lockId))
      .single().block().isReleased()
  }

  @Override
  boolean forceReleaseAll() {
    return flowPublisherToFlux(reactiveConnector.forceReleaseAll())
      .single().block().isReleased()
  }
}
