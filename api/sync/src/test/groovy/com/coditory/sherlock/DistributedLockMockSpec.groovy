package com.coditory.sherlock

import spock.lang.Specification

import static DistributedLockMock.alwaysClosedLock
import static DistributedLockMock.alwaysOpenedLock
import static DistributedLockMock.sequencedLock
import static com.coditory.sherlock.base.DistributedLockAssertions.assertAlwaysClosedLock
import static com.coditory.sherlock.base.DistributedLockAssertions.assertAlwaysOpenedLock

class DistributedLockMockSpec extends Specification {
  String lockId = "sample-lock"

  def "should create always open lock that returns always success"() {
    given:
      DistributedLock lock = alwaysOpenedLock(lockId)
    expect:
      assertAlwaysOpenedLock(lock, lockId)
  }

  def "should create always closed lock that returns always failure"() {
    given:
      DistributedLock lock = alwaysClosedLock("sample-lock")
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
      DistributedLockMock lock = alwaysOpenedLock(lockId)
    expect:
      lock.acquireInvocations() == 0
      lock.releaseInvocations() == 0
    and:
      lock.wasAcquired() == false
      lock.wasReleased() == false

    when:
      lock.acquire()
      lock.release()
    then:
      lock.acquireInvocations() == 1
      lock.releaseInvocations() == 1
    and:
      lock.wasAcquired() == true
      lock.wasReleased() == true
  }
}
