package com.coditory.sherlock.rxjava.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.DistributedLockBuilder
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.common.LockDuration
import com.coditory.sherlock.common.LockId
import com.coditory.sherlock.common.OwnerId
import com.coditory.sherlock.rxjava.RxJavaDistributedLock
import com.coditory.sherlock.rxjava.RxJavaDistributedLockBuilder
import com.coditory.sherlock.rxjava.RxJavaSherlock
import groovy.transform.CompileStatic

import java.time.Duration

@CompileStatic
class BlockingRxJavaSherlock implements Sherlock {
  static Sherlock blockingRxJavaSherlock(RxJavaSherlock locks) {
    return new BlockingRxJavaSherlock(locks)
  }

  private final RxJavaSherlock locks

  private BlockingRxJavaSherlock(RxJavaSherlock locks) {
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

  private DistributedLockBuilder blockingLockBuilder(RxJavaDistributedLockBuilder rxBuilder) {
    return new DistributedLockBuilder({ LockId lockId, LockDuration duration, OwnerId ownerId ->
      RxJavaDistributedLock lock = rxBuilder
        .withLockId(lockId.value)
        .withLockDuration(duration.value)
        .withOwnerId(ownerId.value)
        .build()
      return new BlockingRxJavaLock(lock)
    }).withLockDuration(rxBuilder.getDuration())
      .withOwnerIdPolicy(rxBuilder.getOwnerIdPolicy())
  }
}

@CompileStatic
class BlockingRxJavaLock implements DistributedLock {
  private final RxJavaDistributedLock lock

  BlockingRxJavaLock(RxJavaDistributedLock lock) {
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
