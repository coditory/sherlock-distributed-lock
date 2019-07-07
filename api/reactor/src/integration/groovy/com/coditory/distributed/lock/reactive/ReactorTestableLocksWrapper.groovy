package com.coditory.distributed.lock.reactive

import com.coditory.distributed.lock.DistributedLock
import com.coditory.distributed.lock.common.InstanceId
import com.coditory.distributed.lock.tests.base.TestableDistributedLocks

import java.time.Duration

import static com.coditory.distributed.lock.tests.base.BlockingReactiveDistributedLock.blockingLock

class ReactorTestableLocksWrapper implements TestableDistributedLocks {
  static TestableDistributedLocks testableLocks(ReactiveDistributedLocks locks) {
    return new ReactorTestableLocksWrapper(locks)
  }

  private final ReactiveDistributedLocks locks

  private ReactorTestableLocksWrapper(ReactiveDistributedLocks locks) {
    this.locks = locks
  }

  @Override
  InstanceId getInstanceId() {
    return locks.instanceId
  }

  @Override
  Duration getDefaultDuration() {
    return locks.defaultDuration
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
