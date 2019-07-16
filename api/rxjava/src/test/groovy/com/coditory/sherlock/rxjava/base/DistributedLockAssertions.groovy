package com.coditory.sherlock.rxjava.base


import com.coditory.sherlock.rxjava.RxJavaDistributedLock
import groovy.transform.CompileStatic

import java.time.Duration

@CompileStatic
class DistributedLockAssertions {
  static assertAlwaysOpenedLock(RxJavaDistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, true)
  }

  static assertAlwaysClosedLock(RxJavaDistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, false)
  }

  private static assertSingleStateLock(RxJavaDistributedLock lock, String lockId, boolean expectedResult) {
    assert lock.id == lockId
    assert lock.acquire().blockingGet().acquired == expectedResult
    assert lock.acquire(Duration.ofHours(1)).blockingGet().acquired == expectedResult
    assert lock.acquireForever().blockingGet().acquired == expectedResult
    assert lock.release().blockingGet().unlocked == expectedResult
    return true
  }
}
