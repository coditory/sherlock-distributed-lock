package com.coditory.sherlock.reactor

import spock.lang.Specification

import static com.coditory.sherlock.reactor.ReactorDistributedLockMock.lockStub
import static com.coditory.sherlock.reactor.base.DistributedLockAssertions.assertAlwaysClosedLock
import static com.coditory.sherlock.reactor.base.DistributedLockAssertions.assertAlwaysOpenedLock

class ReactorSherlockStubSpec extends Specification {
  def "should create sherlock returning always opened locks"() {
    given:
      String lockId = "some-lock"
      ReactorSherlock sherlock = ReactorSherlockStub.withReleasedLocks()

    expect:
      assertAlwaysOpenedLock(sherlock.createLock(lockId), lockId)
      assertAlwaysOpenedLock(sherlock.createReentrantLock(lockId), lockId)
      assertAlwaysOpenedLock(sherlock.createOverridingLock(lockId), lockId)
  }

  def "should create sherlock returning always closed locks"() {
    given:
      String lockId = "some-lock"
      ReactorSherlock sherlock = ReactorSherlockStub.withAcquiredLocks()

    expect:
      assertAlwaysClosedLock(sherlock.createLock(lockId), lockId)
      assertAlwaysClosedLock(sherlock.createReentrantLock(lockId), lockId)
      assertAlwaysClosedLock(sherlock.createOverridingLock(lockId), lockId)
  }

  def "should create sherlock returning closed locks by default and opened lock for specific id"() {
    given:
      String lockId = "some-lock"
      ReactorSherlock sherlock = ReactorSherlockStub.withAcquiredLocks()
        .withLock(lockStub(lockId, true))

    expect:
      assertAlwaysClosedLock(sherlock.createLock("other-lock"))
      assertAlwaysOpenedLock(sherlock.createLock(lockId))
  }
}
