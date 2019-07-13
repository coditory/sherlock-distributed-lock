package com.coditory.sherlock.tests.base


import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.common.OwnerId
import com.coditory.sherlock.reactive.ReactiveSherlock

import java.time.Duration

import static BlockingReactiveDistributedLock.blockingLock

class TestableDistributedLocksWrapper implements TestableDistributedLocks {
  static TestableDistributedLocks testableLocks(Sherlock locks) {
    return locks as TestableDistributedLocks
  }

  static TestableDistributedLocks testableLocks(ReactiveSherlock locks) {
    return new TestableDistributedLocksWrapper(locks)
  }

  private final ReactiveSherlock locks;

  private TestableDistributedLocksWrapper(ReactiveSherlock locks) {
    this.locks = locks
  }

  @Override
  OwnerId getOwnerId() {
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
