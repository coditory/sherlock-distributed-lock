package com.coditory.sherlock.rxjava

import spock.lang.Specification

import static RxDistributedLockMock.lockStub
import static com.coditory.sherlock.rxjava.base.DistributedLockAssertions.assertAlwaysClosedLock
import static com.coditory.sherlock.rxjava.base.DistributedLockAssertions.assertAlwaysOpenedLock

class RxSherlockStubSpec extends Specification {
  def "should create sherlock returning always opened locks"() {
    given:
      String lockId = "some-lock"
      RxSherlock sherlock = RxSherlockStub.withReleasedLocks()

    expect:
      assertAlwaysOpenedLock(sherlock.createLock(lockId), lockId)
      assertAlwaysOpenedLock(sherlock.createReentrantLock(lockId), lockId)
      assertAlwaysOpenedLock(sherlock.createOverridingLock(lockId), lockId)
  }

  def "should create sherlock returning always closed locks"() {
    given:
      String lockId = "some-lock"
      RxSherlock sherlock = RxSherlockStub.withAcquiredLocks()

    expect:
      assertAlwaysClosedLock(sherlock.createLock(lockId), lockId)
      assertAlwaysClosedLock(sherlock.createReentrantLock(lockId), lockId)
      assertAlwaysClosedLock(sherlock.createOverridingLock(lockId), lockId)
  }

  def "should create sherlock returning closed locks by default and opened lock for specific id"() {
    given:
      String lockId = "some-lock"
      RxSherlock sherlock = RxSherlockStub.withAcquiredLocks()
        .withLock(lockStub(lockId, true))

    expect:
      assertAlwaysClosedLock(sherlock.createLock("other-lock"))
      assertAlwaysOpenedLock(sherlock.createLock(lockId))
  }
}
