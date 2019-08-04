package com.coditory.sherlock.tests.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.DistributedLockBuilder
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.common.LockDuration
import com.coditory.sherlock.common.LockId
import com.coditory.sherlock.common.OwnerId
import com.coditory.sherlock.reactive.ReactiveDistributedLock
import com.coditory.sherlock.reactive.ReactiveDistributedLockBuilder
import com.coditory.sherlock.reactive.ReactiveSherlock
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

  private DistributedLockBuilder blockingLockBuilder(ReactiveDistributedLockBuilder reactiveBuilder) {
    return new DistributedLockBuilder({ LockId lockId, LockDuration duration, OwnerId ownerId ->
      ReactiveDistributedLock lock = reactiveBuilder
        .withLockId(lockId.value)
        .withLockDuration(duration.value)
        .withOwnerId(ownerId.value)
        .build()
      return new BlockingReactiveDistributedLock(lock)
    }).withLockDuration(reactiveBuilder.getDuration())
      .withOwnerIdPolicy(reactiveBuilder.getOwnerIdPolicy())
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
}
