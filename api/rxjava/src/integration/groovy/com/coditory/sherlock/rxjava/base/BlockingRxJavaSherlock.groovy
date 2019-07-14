package com.coditory.sherlock.rxjava.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.rxjava.RxJavaDistributedLock
import com.coditory.sherlock.rxjava.RxJavaSherlock
import com.coditory.sherlock.tests.base.TestableDistributedLocks
import groovy.transform.CompileStatic

import java.time.Duration

import static BlockingRxJavaLock.blockingLock

@CompileStatic
class BlockingRxJavaSherlock implements TestableDistributedLocks {
  static TestableDistributedLocks blockRxJavaSherlock(RxJavaSherlock locks) {
    return new BlockingRxJavaSherlock(locks)
  }

  private final RxJavaSherlock locks

  private BlockingRxJavaSherlock(RxJavaSherlock locks) {
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
class BlockingRxJavaLock implements DistributedLock {

  static BlockingRxJavaLock blockingLock(RxJavaDistributedLock lock) {
    return new BlockingRxJavaLock(lock)
  }

  private final RxJavaDistributedLock lock

  private BlockingRxJavaLock(RxJavaDistributedLock lock) {
    this.lock = lock
  }

  @Override
  String getId() {
    return lock.id
  }

  @Override
  boolean acquire() {
    return lock.acquire()
        .blockingGet().locked
  }

  @Override
  boolean acquire(Duration duration) {
    return lock.acquire(duration)
        .blockingGet().locked
  }

  @Override
  boolean acquireForever() {
    return lock.acquireForever()
        .blockingGet().locked
  }

  @Override
  boolean release() {
    return lock.release()
        .blockingGet().unlocked
  }
}
