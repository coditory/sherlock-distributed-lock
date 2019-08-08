package com.coditory.sherlock


import spock.lang.Specification
import spock.lang.Unroll

import static ReactorDistributedLockMock.sequencedLock
import static ReactorDistributedLockMock.acquiredInMemoryLock
import static ReactorDistributedLockMock.acquiredReentrantInMemoryLock
import static ReactorDistributedLockMock.lockStub
import static ReactorDistributedLockMock.releasedInMemoryLock

class ReactorDistributedLockMockSpec extends Specification {
  private static final String lockId = "sample-lock"

  @Unroll
  def "should create single state lock with #action"() {
    expect:
      acquire(lock) == acquireResult
      acquire(lock) == acquireResult
    and:
      release(lock) == releaseResult
      release(lock) == releaseResult
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
      ReactorDistributedLock lock = sequencedLock("sample-lock", acquireResultSequence, releaseResultSequence)
    expect:
      [acquire(lock), acquire(lock), acquire(lock), acquire(lock)] == acquireResultSequence + true
      [release(lock), release(lock), release(lock), release(lock)] == releaseResultSequence + false
  }

  @Unroll
  def "should create a released in-memory lock"() {
    expect:
      release(lock) == false
      acquire(lock) == true
    where:
      lock << [
        releasedInMemoryLock()
//        releasedReentrantInMemoryLock(),
//        releasedInMemoryLock(lockId),
//        releasedReentrantInMemoryLock(lockId)
      ]
  }

  @Unroll
  def "should create an acquired reentrant in-memory lock"() {
    expect:
      release(lock) == true
      acquire(lock) == true
      acquire(lock) == true
    where:
      lock << [
        acquiredReentrantInMemoryLock(),
        acquiredReentrantInMemoryLock(lockId)
      ]
  }

  @Unroll
  def "should create an acquired single entrant in-memory lock"() {
    expect:
      release(lock) == true
      acquire(lock) == true
      acquire(lock) == false
    where:
      lock << [
        acquiredInMemoryLock(),
        acquiredInMemoryLock(lockId)
      ]
  }

  def "should record no invocation for a new lock mock instance"() {
    given:
      ReactorDistributedLockMock lock = releasedInMemoryLock()
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
      ReactorDistributedLockMock lock = releasedInMemoryLock()
    when:
      acquire(lock)
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
      acquire(lock)
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
      ReactorDistributedLockMock lock = acquiredInMemoryLock()
    when:
      release(lock)
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
      release(lock)
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
      ReactorDistributedLockMock lock = releasedInMemoryLock()
    when:
      acquire(lock)
      release(lock)
    then:
      lock.wasAcquiredAndReleased() == true
  }

  private Boolean acquire(ReactorDistributedLock lock) {
    return lock.acquire().block().acquired
  }

  private Boolean release(ReactorDistributedLock lock) {
    return lock.release().block().released
  }
}