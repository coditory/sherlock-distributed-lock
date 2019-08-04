package com.coditory.sherlock.rxjava.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.DistributedLockBuilder
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.common.LockDuration
import com.coditory.sherlock.common.LockId
import com.coditory.sherlock.common.OwnerId
import com.coditory.sherlock.rxjava.RxDistributedLock
import com.coditory.sherlock.rxjava.RxDistributedLockBuilder
import com.coditory.sherlock.rxjava.RxSherlock
import groovy.transform.CompileStatic

import java.time.Duration

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
    return locks.forceReleaseAllLocks()
      .blockingGet().released
  }

  @Override
  boolean forceReleaseLock(String lockId) {
    return locks.forceReleaseLock(lockId)
      .blockingGet().released
  }

  private DistributedLockBuilder blockingLockBuilder(RxDistributedLockBuilder rxBuilder) {
    return new DistributedLockBuilder({ LockId lockId, LockDuration duration, OwnerId ownerId ->
      RxDistributedLock lock = rxBuilder
        .withLockId(lockId.value)
        .withLockDuration(duration.value)
        .withOwnerId(ownerId.value)
        .build()
      return new BlockingRxLock(lock)
    }).withLockDuration(rxBuilder.getDuration())
      .withOwnerIdPolicy(rxBuilder.getOwnerIdPolicy())
  }
}

@CompileStatic
class BlockingRxLock implements DistributedLock {
  private final RxDistributedLock lock

  BlockingRxLock(RxDistributedLock lock) {
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
