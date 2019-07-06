package com.coditory.distributed.lock.tests.base

import com.coditory.distributed.lock.DistributedLock
import com.coditory.distributed.lock.DistributedLocks
import com.coditory.distributed.lock.common.InstanceId
import com.coditory.distributed.lock.reactive.ReactiveDistributedLocks

import java.time.Duration

import static com.coditory.distributed.lock.tests.base.BlockingReactiveDistributedLock.blockingLock

class TestableDistributedLocksWrapper implements TestableDistributedLocks {
  static TestableDistributedLocks testableLocks(DistributedLocks locks) {
    return locks as TestableDistributedLocks
  }

  static TestableDistributedLocks testableLocks(ReactiveDistributedLocks locks) {
    return new TestableDistributedLocksWrapper(locks)
  }

  private final ReactiveDistributedLocks locks;

  private TestableDistributedLocksWrapper(ReactiveDistributedLocks locks) {
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
