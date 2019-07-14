package com.coditory.sherlock.tests.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.reactive.ReactiveDistributedLock
import com.coditory.sherlock.reactive.ReactiveSherlock
import groovy.transform.CompileStatic

import java.time.Duration

import static BlockingReactiveDistributedLock.blockingLock
import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux

@CompileStatic
class BlockingReactiveSherlockWrapper implements TestableDistributedLocks {
  static TestableDistributedLocks testableLocks(Sherlock locks) {
    return locks as TestableDistributedLocks
  }

  static TestableDistributedLocks testableLocks(ReactiveSherlock locks) {
    return new BlockingReactiveSherlockWrapper(locks)
  }

  private final ReactiveSherlock locks;

  private BlockingReactiveSherlockWrapper(ReactiveSherlock locks) {
    this.locks = locks
  }

  @Override
  String getOwnerId() {
    return locks.ownerId
  }

  @Override
  Duration getDefaultDuration() {
    return locks.lockDuration
  }

  @Override
  DistributedLock createReentrantLock(String lockId) {
    return blockingLock(locks.createReentrantLock(lockId))
  }

  @Override
  DistributedLock createReentrantLock(String lockId, Duration duration) {
    return blockingLock(locks.createReentrantLock(lockId, duration))
  }

  @Override
  DistributedLock createLock(String lockId) {
    return blockingLock(locks.createLock(lockId))
  }

  @Override
  DistributedLock createLock(String lockId, Duration duration) {
    return blockingLock(locks.createLock(lockId, duration))
  }

  @Override
  DistributedLock createOverridingLock(String lockId) {
    return blockingLock(locks.createOverridingLock(lockId))
  }

  @Override
  DistributedLock createOverridingLock(String lockId, Duration duration) {
    return blockingLock(locks.createOverridingLock(lockId, duration))
  }
}


@CompileStatic
class BlockingReactiveDistributedLock implements DistributedLock {
  static BlockingReactiveDistributedLock blockingLock(ReactiveDistributedLock lock) {
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
