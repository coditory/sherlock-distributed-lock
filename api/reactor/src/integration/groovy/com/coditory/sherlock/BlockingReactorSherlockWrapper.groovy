package com.coditory.sherlock


import groovy.transform.CompileStatic

import java.time.Duration

import static com.coditory.sherlock.BlockingReactorLock.blockingReactorLock

@CompileStatic
class BlockingReactorSherlockWrapper implements Sherlock {
  static Sherlock blockingReactorSherlock(ReactorSherlock locks) {
    return new BlockingReactorSherlockWrapper(locks)
  }

  private final ReactorSherlock locks

  private BlockingReactorSherlockWrapper(ReactorSherlock locks) {
    this.locks = locks
  }

  @Override
  void initialize() {
    locks.initialize().block()
  }

  @Override
  DistributedLockBuilder<DistributedLock> createLock() {
    return blockingLockBuilder(locks.createLock())
  }

  @Override
  DistributedLockBuilder<DistributedLock> createReentrantLock() {
    return blockingLockBuilder(locks.createReentrantLock())
  }

  @Override
  DistributedLockBuilder<DistributedLock> createOverridingLock() {
    return blockingLockBuilder(locks.createOverridingLock())
  }

  @Override
  boolean forceReleaseAllLocks() {
    return locks.forceReleaseAllLocks().block()
      .released
  }

  @Override
  boolean forceReleaseLock(String lockId) {
    return locks.forceReleaseLock(lockId).block()
      .released
  }

  private DistributedLockBuilder<DistributedLock> blockingLockBuilder(DistributedLockBuilder<ReactorDistributedLock> reactorBuilder) {
    return reactorBuilder.withMappedLock({ lock -> blockingReactorLock(lock) })
  }
}

@CompileStatic
class BlockingReactorLock implements DistributedLock {
  static DistributedLock blockingReactorLock(ReactorDistributedLock lock) {
    return new BlockingReactorLock(lock)
  }

  private final ReactorDistributedLock lock

  private BlockingReactorLock(ReactorDistributedLock lock) {
    this.lock = lock
  }

  @Override
  String getId() {
    return lock.id
  }

  @Override
  boolean acquire() {
    return lock.acquire()
      .block().acquired
  }

  @Override
  boolean acquire(Duration duration) {
    return lock.acquire(duration)
      .block().acquired
  }

  @Override
  boolean acquireForever() {
    return lock.acquireForever()
      .block().acquired
  }

  @Override
  boolean release() {
    return lock.release()
      .block().released
  }
}
