package com.coditory.sherlock.reactor.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.reactor.ReactorDistributedLock
import com.coditory.sherlock.reactor.ReactorSherlock
import com.coditory.sherlock.tests.base.TestableDistributedLocks
import groovy.transform.CompileStatic

import java.time.Duration

import static BlockingReactorLock.blockingLock

@CompileStatic
class BlockingReactorSherlockWrapper implements TestableDistributedLocks {
  static TestableDistributedLocks blockReactorSherlock(ReactorSherlock locks) {
    return new BlockingReactorSherlockWrapper(locks)
  }

  private final ReactorSherlock locks

  private BlockingReactorSherlockWrapper(ReactorSherlock locks) {
    this.locks = locks
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
class BlockingReactorLock implements DistributedLock {
  static BlockingReactorLock blockingLock(ReactorDistributedLock lock) {
    return new BlockingReactorLock(lock)
  }

  private final ReactorDistributedLock lock

  BlockingReactorLock(ReactorDistributedLock lock) {
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
        .block().unlocked
  }
}
