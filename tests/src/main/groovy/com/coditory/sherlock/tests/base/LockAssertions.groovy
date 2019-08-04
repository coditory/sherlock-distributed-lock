package com.coditory.sherlock.tests.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.tests.LocksBaseSpec
import groovy.transform.CompileStatic
import groovy.transform.SelfType

import java.time.Duration
import java.time.Instant

import static com.coditory.sherlock.common.util.UuidGenerator.uuid
import static com.coditory.sherlock.tests.base.LockTypes.SINGLE_ENTRANT

@CompileStatic
@SelfType(LocksBaseSpec)
trait LockAssertions {
  boolean assertAcquired(String lockId) {
    DistributedLock otherLock = createLockWithRandomOwner(lockId)
    assert otherLock.acquire() == false
    return true
  }

  boolean assertAcquired(String lockId, Duration duration) {
    DistributedLock otherLock = createLockWithRandomOwner(lockId)
    assert otherLock.acquire() == false
    Instant backup = fixedClock.instant()
    fixedClock.tick(duration - Duration.ofMillis(1))
    assert otherLock.acquire() == false
    fixedClock.tick(Duration.ofMillis(1))
    assert otherLock.acquire() == true
    otherLock.release()
    fixedClock.setup(backup)
    return true
  }

  boolean assertAcquiredForever(String lockId) {
    DistributedLock otherLock = createLockWithRandomOwner(lockId)
    assert otherLock.acquire() == false
    Instant backup = fixedClock.instant()
    fixedClock.tick(Duration.ofDays(100_000))
    assert otherLock.acquire() == false
    fixedClock.setup(backup)
    return true
  }

  boolean assertReleased(String lockId) {
    DistributedLock otherLock = createLockWithRandomOwner(lockId)
    assert otherLock.acquire() == true
    otherLock.release()
    return true
  }

  private DistributedLock createLockWithRandomOwner(String lockId) {
    return createLock(SINGLE_ENTRANT, lockId, uuid())
  }
}
