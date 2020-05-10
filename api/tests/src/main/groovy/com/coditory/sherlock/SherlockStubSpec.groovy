package com.coditory.sherlock

import spock.lang.Specification

import static com.coditory.sherlock.DistributedLockMock.lockStub
import static com.coditory.sherlock.SherlockStub.sherlockWithAcquiredLocks
import static com.coditory.sherlock.SherlockStub.sherlockWithReleasedLocks

abstract class SherlockStubSpec extends Specification {
  def "should create sherlock returning always opened locks"() {
    given:
      String lockId = "some-lock"
      Sherlock sherlock = sherlockWithReleasedLocks()

    expect:
      assertAlwaysOpenedLock(sherlock.createLock(lockId), lockId)
      assertAlwaysOpenedLock(sherlock.createReentrantLock(lockId), lockId)
      assertAlwaysOpenedLock(sherlock.createOverridingLock(lockId), lockId)
  }

  def "should create sherlock returning always closed locks"() {
    given:
      String lockId = "some-lock"
      Sherlock sherlock = sherlockWithAcquiredLocks()

    expect:
      assertAlwaysClosedLock(sherlock.createLock(lockId), lockId)
      assertAlwaysClosedLock(sherlock.createReentrantLock(lockId), lockId)
      assertAlwaysClosedLock(sherlock.createOverridingLock(lockId), lockId)
  }

  def "should create sherlock returning closed locks by default and opened lock for specific id"() {
    given:
      String lockId = "some-lock"
      Sherlock sherlock = sherlockWithAcquiredLocks()
        .sherlockWithLock(lockStub(lockId, true))
    expect:
      sherlock.createLock("other-lock").acquire() == false
    and:
      sherlock.createLock(lockId).acquire() == true
  }

  static assertAlwaysOpenedLock(DistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, true)
  }

  static assertAlwaysClosedLock(DistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, false)
  }

  private static assertSingleStateLock(DistributedLock lock, String lockId, boolean expectedResult) {
    assert lock.id == lockId
    assert lock.acquire() == expectedResult
    assert lock.release() == expectedResult
    return true
  }
}
