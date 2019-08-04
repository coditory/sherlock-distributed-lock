package com.coditory.sherlock.reactor.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.DistributedLockBuilder
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.common.LockDuration
import com.coditory.sherlock.common.LockId
import com.coditory.sherlock.common.OwnerId
import com.coditory.sherlock.reactor.ReactorDistributedLock
import com.coditory.sherlock.reactor.ReactorDistributedLockBuilder
import com.coditory.sherlock.reactor.ReactorSherlock
import groovy.transform.CompileStatic

import java.time.Duration

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
    return locks.forceReleaseAllLocks().block()
      .released
  }

  @Override
  boolean forceReleaseLock(String lockId) {
    return locks.forceReleaseLock(lockId).block()
      .released
  }

  private DistributedLockBuilder blockingLockBuilder(ReactorDistributedLockBuilder reactorBuilder) {
    return new DistributedLockBuilder({ LockId lockId, LockDuration duration, OwnerId ownerId ->
      ReactorDistributedLock lock = reactorBuilder
        .withLockId(lockId.value)
        .withLockDuration(duration.value)
        .withOwnerId(ownerId.value)
        .build()
      return new BlockingReactorLock(lock)
    }).withLockDuration(reactorBuilder.getDuration())
      .withOwnerIdPolicy(reactorBuilder.getOwnerIdPolicy())
  }
}

@CompileStatic
class BlockingReactorLock implements DistributedLock {
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
      .block().released
  }
}
