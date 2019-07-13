package com.coditory.sherlock

import com.coditory.sherlock.test.DistributedLockMock
import spock.lang.Specification

import static com.coditory.sherlock.test.DistributedLockMock.alwaysAcquiredLock
import static com.coditory.sherlock.test.DistributedLockMock.alwaysReleasedLock
import static com.coditory.sherlock.test.DistributedLockMock.sequencedLock
import static com.coditory.sherlock.base.DistributedLockAssertions.assertAlwaysClosedLock
import static com.coditory.sherlock.base.DistributedLockAssertions.assertAlwaysOpenedLock

class DistributedLockMockSpec extends Specification {
  String lockId = "sample-lock"

  def "should create always open lock that returns always success"() {
    given:
      DistributedLock lock = alwaysReleasedLock(lockId)
    expect:
      assertAlwaysOpenedLock(lock, lockId)
  }

  def "should create always closed lock that returns always failure"() {
    given:
      DistributedLock lock = alwaysAcquiredLock("sample-lock")
    expect:
      assertAlwaysClosedLock(lock, lockId)
  }

  def "should create a lock stub that returns a sequence of results"() {
    given:
      List<Boolean> resultSequence = [true, false, true]
    and:
      DistributedLock lock = sequencedLock("sample-lock", resultSequence)
    expect:
      [lock.acquire(), lock.acquire(), lock.acquire()] == resultSequence
      [lock.release(), lock.release(), lock.release()] == resultSequence
  }

  def "should create a lock stub that returns a sequence of different results"() {
    given:
      List<Boolean> acquireResultSequence = [true, false, true]
      List<Boolean> releaseResultSequence = [false, false, true]
    and:
      DistributedLock lock = sequencedLock("sample-lock", acquireResultSequence, releaseResultSequence)
    expect:
      [lock.acquire(), lock.acquire(), lock.acquire()] == acquireResultSequence
      [lock.release(), lock.release(), lock.release()] == releaseResultSequence
  }

  def "should count acquire and release invocations"() {
    given:
      DistributedLockMock lock = alwaysReleasedLock(lockId)
    expect:
      lock.acquireInvocations() == 0
      lock.releaseInvocations() == 0
    and:
      lock.wasAcquireInvoked() == false
      lock.wasReleaseInvoked() == false

    when:
      lock.acquire()
      lock.release()
    then:
      lock.acquireInvocations() == 1
      lock.releaseInvocations() == 1
    and:
      lock.wasAcquireInvoked() == true
      lock.wasReleaseInvoked() == true
  }
}
