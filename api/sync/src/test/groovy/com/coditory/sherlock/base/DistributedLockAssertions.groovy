package com.coditory.sherlock.base

import com.coditory.sherlock.DistributedLock

import java.time.Duration

class DistributedLockAssertions {
  static assertAlwaysOpenedLock(DistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, true)
  }

  static assertAlwaysClosedLock(DistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, false)
  }

  private static assertSingleStateLock(DistributedLock lock, String lockId, boolean expectedResult) {
    assert lock.id == lockId
    assert lock.acquire() == expectedResult
    assert lock.acquire(Duration.ofHours(1)) == expectedResult
    assert lock.acquireForever() == expectedResult
    assert lock.release() == expectedResult
    return true
  }
}
