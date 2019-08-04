package com.coditory.sherlock

import spock.lang.Specification
import spock.lang.Unroll

import static com.coditory.sherlock.DistributedLockMock.acquiredInMemoryLock
import static com.coditory.sherlock.DistributedLockMock.acquiredReentrantInMemoryLock
import static com.coditory.sherlock.DistributedLockMock.releasedInMemoryLock
import static com.coditory.sherlock.DistributedLockMock.releasedReentrantInMemoryLock
import static com.coditory.sherlock.DistributedLockMock.sequencedLock
import static com.coditory.sherlock.DistributedLockMock.lockStub

class DistributedLockMockSpec extends Specification {
  private static final String lockId = "sample-lock"

  @Unroll
  def "should create single state lock with #action"() {
    expect:
      lock.acquire() == acquireResult
      lock.acquire() == acquireResult
    and:
      lock.release() == releaseResult
      lock.release() == releaseResult
    where:
      action                                 | acquireResult | releaseResult | lock
      'singleStateLock(lockId, true)'        | true          | true          | lockStub(lockId, true)
      'singleStateLock(lockId, true, false)' | true          | false         | lockStub(lockId, true, false)
      'singleStateLock(true)'                | false         | false         | lockStub(false)
      'singleStateLock(true, false)'         | false         | true          | lockStub(false, true)
  }

  def "should create a lock that returns a sequence of results"() {
    given:
      List<Boolean> acquireResultSequence = [true, false, true]
      List<Boolean> releaseResultSequence = [false, true, false]
    and:
      DistributedLock lock = sequencedLock("sample-lock", acquireResultSequence, releaseResultSequence)
    expect:
      [lock.acquire(), lock.acquire(), lock.acquire(), lock.acquire()] == acquireResultSequence + true
      [lock.release(), lock.release(), lock.release(), lock.release()] == releaseResultSequence + false
  }

  @Unroll
  def "should create a released in-memory lock"() {
    expect:
      lock.release() == false
      lock.acquire() == true
    where:
      lock << [
        releasedInMemoryLock(),
        releasedReentrantInMemoryLock(),
        releasedInMemoryLock(lockId),
        releasedReentrantInMemoryLock(lockId)
      ]
  }

  @Unroll
  def "should create an acquired reentrant in-memory lock"() {
    expect:
      lock.release() == true
      lock.acquire() == true
      lock.acquire() == true
    where:
      lock << [
        acquiredReentrantInMemoryLock(),
        acquiredReentrantInMemoryLock(lockId)
      ]
  }

  @Unroll
  def "should create an acquired single entrant in-memory lock"() {
    expect:
      lock.release() == true
      lock.acquire() == true
      lock.acquire() == false
    where:
      lock << [
        acquiredInMemoryLock(),
        acquiredInMemoryLock(lockId)
      ]
  }

  def "should record no invocation for a new lock mock instance"() {
    given:
      DistributedLockMock lock = releasedInMemoryLock()
    expect:
      lock.acquisitions() == 0
      lock.releases() == 0
    and:
      lock.successfulAcquisitions() == 0
      lock.successfulReleases() == 0
    and:
      lock.wasAcquireInvoked() == false
      lock.wasReleaseInvoked() == false
      lock.wasAcquireRejected() == false
      lock.wasReleaseRejected() == false
      lock.wasAcquiredAndReleased() == false
  }

  def "should record lock acquire invocations"() {
    given:
      DistributedLockMock lock = releasedInMemoryLock()
    when:
      lock.acquire()
    then:
      lock.acquisitions() == 1
    and:
      lock.successfulAcquisitions() == 1
      lock.rejectedAcquisitions() == 0
    and:
      lock.wasAcquireInvoked() == true
      lock.wasAcquireRejected() == false
      lock.wasAcquiredAndReleased() == false

    when:
      lock.acquire()
    then:
      lock.acquisitions() == 2
    and:
      lock.successfulAcquisitions() == 1
      lock.rejectedAcquisitions() == 1
    and:
      lock.wasAcquireInvoked() == true
      lock.wasAcquireRejected() == true
  }

  def "should record lock release invocations"() {
    given:
      DistributedLockMock lock = acquiredInMemoryLock()
    when:
      lock.release()
    then:
      lock.releases() == 1
    and:
      lock.successfulReleases() == 1
      lock.rejectedReleases() == 0
    and:
      lock.wasReleaseInvoked() == true
      lock.wasReleaseRejected() == false
      lock.wasAcquiredAndReleased() == false

    when:
      lock.release()
    then:
      lock.releases() == 2
    and:
      lock.successfulReleases() == 1
      lock.rejectedReleases() == 1
    and:
      lock.wasReleaseInvoked() == true
      lock.wasReleaseRejected() == true
  }

  def "should record acquire and release invocations"() {
    given:
      DistributedLockMock lock = releasedInMemoryLock()
    when:
      lock.acquire()
      lock.release()
    then:
      lock.wasAcquiredAndReleased() == true
  }
}
