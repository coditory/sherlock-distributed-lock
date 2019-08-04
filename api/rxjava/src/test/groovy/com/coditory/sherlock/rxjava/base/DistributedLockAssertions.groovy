package com.coditory.sherlock.rxjava.base


import com.coditory.sherlock.rxjava.RxDistributedLock
import groovy.transform.CompileStatic

import java.time.Duration

@CompileStatic
class DistributedLockAssertions {
  static assertAlwaysOpenedLock(RxDistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, true)
  }

  static assertAlwaysClosedLock(RxDistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, false)
  }

  private static assertSingleStateLock(RxDistributedLock lock, String lockId, boolean expectedResult) {
    assert lock.id == lockId
    assert lock.acquire().blockingGet().acquired == expectedResult
    assert lock.acquire(Duration.ofHours(1)).blockingGet().acquired == expectedResult
    assert lock.acquireForever().blockingGet().acquired == expectedResult
    assert lock.release().blockingGet().released == expectedResult
    return true
  }
}
