package com.coditory.sherlock.reactor


import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.common.InstanceId
import com.coditory.sherlock.reactive.ReactiveSherlock
import com.coditory.sherlock.tests.base.TestableDistributedLocks

import java.time.Duration

import static com.coditory.sherlock.tests.base.BlockingReactiveDistributedLock.blockingLock

class ReactorTestableLocksWrapper implements TestableDistributedLocks {
  static TestableDistributedLocks testableLocks(ReactiveSherlock locks) {
    return new ReactorTestableLocksWrapper(locks)
  }

  private final ReactiveSherlock locks

  private ReactorTestableLocksWrapper(ReactiveSherlock locks) {
    this.locks = locks
  }

  @Override
  InstanceId getInstanceId() {
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

class ReatorBlockingLock implements DistributedLock {
  static blockingLock(ReactorDistributedLock lock) {
    return new ReatorBlockingLock(lock)
  }

  private final ReactorDistributedLock lock

  ReatorBlockingLock(ReactorDistributedLock lock) {
    this.lock = lock
  }

  @Override
  String getId() {
    return lock.id
  }

  @Override
  boolean acquire() {
    return lock.acquire()
        .block().locked
  }

  @Override
  boolean acquire(Duration duration) {
    return lock.acquire(duration)
        .block().locked
  }

  @Override
  boolean acquireForever() {
    return lock.acquireForever()
        .block().locked
  }

  @Override
  boolean release() {
    return lock.release()
        .block().unlocked
  }
}
