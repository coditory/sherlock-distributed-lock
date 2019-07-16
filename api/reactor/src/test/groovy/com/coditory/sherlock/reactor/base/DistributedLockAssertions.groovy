package com.coditory.sherlock.reactor.base

import com.coditory.sherlock.reactor.ReactorDistributedLock
import groovy.transform.CompileStatic

import java.time.Duration

@CompileStatic
class DistributedLockAssertions {
  static assertAlwaysOpenedLock(ReactorDistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, true)
  }

  static assertAlwaysClosedLock(ReactorDistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, false)
  }

  private static assertSingleStateLock(ReactorDistributedLock lock, String lockId, boolean expectedResult) {
    assert lock.id == lockId
    assert lock.acquire().block().acquired == expectedResult
    assert lock.acquire(Duration.ofHours(1)).block().acquired == expectedResult
    assert lock.acquireForever().block().acquired == expectedResult
    assert lock.release().block().unlocked == expectedResult
    return true
  }
}
