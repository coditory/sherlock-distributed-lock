package com.coditory.sherlock.reactive

import spock.lang.Specification

import java.time.Duration

import static com.coditory.sherlock.reactive.ReactorDistributedLockMock.alwaysOpenedLock
import static com.coditory.sherlock.reactive.base.DistributedLockAssertions.assertAlwaysClosedLock
import static com.coditory.sherlock.reactive.base.DistributedLockAssertions.assertAlwaysOpenedLock

class ReactorSherlockStubSpec extends Specification {
  def "should create sharelock with custom properties"() {
    given:
      String instanceId = "tested-instance-id"
      Duration duration = Duration.ofHours(1)

    when:
      ReactorSherlock sharelock = ReactorSherlockStub.withOpenedLocks()
          .withLockDuration(duration)
          .withServiceInstanceId(instanceId)

    then:
      sharelock.lockDuration == duration
      sharelock.instanceId == instanceId
  }

  def "should create sharelock returning always opened locks"() {
    given:
      String lockId = "some-lock"
      ReactorSherlock sharelock = ReactorSherlockStub.withOpenedLocks()

    expect:
      assertAlwaysOpenedLock(sharelock.createLock(lockId), lockId)
      assertAlwaysOpenedLock(sharelock.createReentrantLock(lockId), lockId)
      assertAlwaysOpenedLock(sharelock.createOverridingLock(lockId), lockId)
  }

  def "should create sharelock returning always closed locks"() {
    given:
      String lockId = "some-lock"
      ReactorSherlock sharelock = ReactorSherlockStub.withClosedLocks()

    expect:
      assertAlwaysClosedLock(sharelock.createLock(lockId), lockId)
      assertAlwaysClosedLock(sharelock.createReentrantLock(lockId), lockId)
      assertAlwaysClosedLock(sharelock.createOverridingLock(lockId), lockId)
  }

  def "should create sharelock returning closed locks by default and opened lock for specific id"() {
    given:
      String lockId = "some-lock"
      ReactorSherlock sharelock = ReactorSherlockStub.withClosedLocks()
          .withLock(alwaysOpenedLock(lockId))

    expect:
      assertAlwaysClosedLock(sharelock.createLock("other-lock"))
      assertAlwaysOpenedLock(sharelock.createLock(lockId))
  }
}
