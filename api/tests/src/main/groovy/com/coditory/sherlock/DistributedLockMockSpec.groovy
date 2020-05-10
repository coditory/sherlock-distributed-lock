package com.coditory.sherlock

import com.coditory.sherlock.base.SpecDistributedLockMock
import com.coditory.sherlock.base.SpecLockMockFactory
import spock.lang.Specification
import spock.lang.Unroll

import static java.util.Objects.requireNonNull

abstract class DistributedLockMockSpec extends Specification {
  private static final String lockId = "sample-lock"

  private final SpecLockMockFactory factory

  DistributedLockMockSpec(SpecLockMockFactory factory) {
    this.factory = requireNonNull(factory);
  }

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
      'singleStateLock(lockId, true)'        | true          | true          | factory.lockStub(lockId, true)
      'singleStateLock(lockId, true, false)' | true          | false         | factory.lockStub(lockId, true, false)
      'singleStateLock(true)'                | false         | false         | factory.lockStub(false)
      'singleStateLock(true, false)'         | false         | true          | factory.lockStub(false, true)
  }

  def "should create a lock that returns a sequence of results"() {
    given:
      List<Boolean> acquireResultSequence = [true, false, true]
      List<Boolean> releaseResultSequence = [false, true, false]
    and:
      SpecDistributedLockMock lock = factory.sequencedLock("sample-lock", acquireResultSequence, releaseResultSequence)
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
          factory.releasedInMemoryLock(),
          factory.releasedReentrantInMemoryLock(),
          factory.releasedInMemoryLock(lockId),
          factory.releasedReentrantInMemoryLock(lockId)
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
          factory.acquiredReentrantInMemoryLock(),
          factory.acquiredReentrantInMemoryLock(lockId)
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
          factory.acquiredInMemoryLock(),
          factory.acquiredInMemoryLock(lockId)
      ]
  }

  def "should record no invocation for a new lock mock instance"() {
    given:
      SpecDistributedLockMock lock = factory.releasedInMemoryLock()
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
      SpecDistributedLockMock lock = factory.releasedInMemoryLock()
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
      SpecDistributedLockMock lock = factory.acquiredInMemoryLock()
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
      SpecDistributedLockMock lock = factory.releasedInMemoryLock()
    when:
      lock.acquire()
      lock.release()
    then:
      lock.wasAcquiredAndReleased() == true
  }
}
