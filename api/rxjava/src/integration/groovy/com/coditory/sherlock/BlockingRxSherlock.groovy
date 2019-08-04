package com.coditory.sherlock


import groovy.transform.CompileStatic

import java.time.Duration

import static com.coditory.sherlock.BlockingRxLock.blockingRxLock

@CompileStatic
class BlockingRxSherlock implements Sherlock {
  static Sherlock blockingRxJavaSherlock(RxSherlock locks) {
    return new BlockingRxSherlock(locks)
  }

  private final RxSherlock locks

  private BlockingRxSherlock(RxSherlock locks) {
    this.locks = locks
  }

  @Override
  void initialize() {
    locks.initialize().blockingGet()
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
    return locks.forceReleaseAllLocks()
      .blockingGet().released
  }

  @Override
  boolean forceReleaseLock(String lockId) {
    return locks.forceReleaseLock(lockId)
      .blockingGet().released
  }

  private DistributedLockBuilder<DistributedLock> blockingLockBuilder(DistributedLockBuilder<RxDistributedLock> rxBuilder) {
    return rxBuilder.withMappedLock({ lock -> blockingRxLock(lock) })
  }
}

@CompileStatic
class BlockingRxLock implements DistributedLock {
  static DistributedLock blockingRxLock(RxDistributedLock lock) {
    return new BlockingRxLock(lock)
  }

  private final RxDistributedLock lock

  private BlockingRxLock(RxDistributedLock lock) {
    this.lock = lock
  }

  @Override
  String getId() {
    return lock.id
  }

  @Override
  boolean acquire() {
    return lock.acquire()
      .blockingGet().acquired
  }

  @Override
  boolean acquire(Duration duration) {
    return lock.acquire(duration)
      .blockingGet().acquired
  }

  @Override
  boolean acquireForever() {
    return lock.acquireForever()
      .blockingGet().acquired
  }

  @Override
  boolean release() {
    return lock.release()
      .blockingGet().released
  }
}
