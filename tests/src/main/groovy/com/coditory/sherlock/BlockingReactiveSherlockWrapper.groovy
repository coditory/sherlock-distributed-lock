package com.coditory.sherlock


import groovy.transform.CompileStatic

import java.time.Duration

import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux

// @CompileStatic - groovy compiler throws StackOverflow when uncommented
// it's probably related with implementing an interface with default methods
class BlockingReactiveSherlockWrapper implements Sherlock {
  static Sherlock blockingReactiveSherlock(ReactiveSherlock locks) {
    return new BlockingReactiveSherlockWrapper(locks)
  }

  private final ReactiveSherlock locks

  private BlockingReactiveSherlockWrapper(ReactiveSherlock locks) {
    this.locks = locks
  }

  @Override
  void initialize() {
    flowPublisherToFlux(locks.initialize())
      .single().block()
  }

  @Override
  DistributedLockBuilder createLock() {
    return blockingLockBuilder(locks.createLock())
  }

  @Override
  DistributedLockBuilder createReentrantLock() {
    return blockingLockBuilder(locks.createReentrantLock())
  }

  @Override
  DistributedLockBuilder createOverridingLock() {
    return blockingLockBuilder(locks.createOverridingLock())
  }

  @Override
  boolean forceReleaseAllLocks() {
    return flowPublisherToFlux(locks.forceReleaseAllLocks())
      .single().block().released
  }

  @Override
  boolean forceReleaseLock(String lockId) {
    return createOverridingLock(lockId)
      .release()
  }

  private DistributedLockBuilder blockingLockBuilder(
    DistributedLockBuilder<ReactiveDistributedLock> reactiveBuilder) {
    return reactiveBuilder.withMappedLock({ lock -> new BlockingReactiveDistributedLock(lock) })
  }
}


@CompileStatic
class BlockingReactiveDistributedLock implements DistributedLock {
  private final ReactiveDistributedLock lock

  BlockingReactiveDistributedLock(ReactiveDistributedLock lock) {
    this.lock = lock
  }

  @Override
  String getId() {
    return lock.id
  }

  @Override
  boolean acquire() {
    return flowPublisherToFlux(lock.acquire())
      .single().block().acquired
  }

  @Override
  boolean acquire(Duration duration) {
    return flowPublisherToFlux(lock.acquire(duration))
      .single().block().acquired
  }

  @Override
  boolean acquireForever() {
    return flowPublisherToFlux(lock.acquireForever())
      .single().block().acquired
  }

  @Override
  boolean release() {
    return flowPublisherToFlux(lock.release())
      .single().block().released
  }

  @Override
  boolean isAcquired() {
    return flowPublisherToFlux(lock.isAcquired())
        .single().block()
  }

  @Override
  boolean isLocked() {
    return flowPublisherToFlux(lock.isLocked())
        .single().block()
  }

  @Override
  boolean isUnlocked() {
    return flowPublisherToFlux(lock.isReleased())
        .single().block()
  }
}
