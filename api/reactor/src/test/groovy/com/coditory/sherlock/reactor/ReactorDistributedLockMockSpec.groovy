package com.coditory.sherlock.reactor

import com.coditory.sherlock.reactive.connector.LockResult
import com.coditory.sherlock.reactive.connector.ReleaseResult
import com.coditory.sherlock.reactor.test.ReactorDistributedLockMock
import reactor.core.publisher.Mono
import spock.lang.Specification

import static com.coditory.sherlock.reactor.test.ReactorDistributedLockMock.alwaysAcquiredLock
import static com.coditory.sherlock.reactor.test.ReactorDistributedLockMock.alwaysReleasedLock
import static com.coditory.sherlock.reactor.test.ReactorDistributedLockMock.sequencedLock
import static com.coditory.sherlock.reactor.base.DistributedLockAssertions.assertAlwaysClosedLock
import static com.coditory.sherlock.reactor.base.DistributedLockAssertions.assertAlwaysOpenedLock

class ReactorDistributedLockMockSpec extends Specification {
  String lockId = "sample-lock"

  def "should create always open lock that returns always success"() {
    given:
      ReactorDistributedLock lock = alwaysReleasedLock(lockId)
    expect:
      assertAlwaysOpenedLock(lock, lockId)
  }

  def "should create always closed lock that returns always failure"() {
    given:
      ReactorDistributedLock lock = alwaysAcquiredLock("sample-lock")
    expect:
      assertAlwaysClosedLock(lock, lockId)
  }

  def "should create a lock stub that returns a sequence of results"() {
    given:
      List<Boolean> resultSequence = [true, false, true]
    and:
      ReactorDistributedLock lock = sequencedLock("sample-lock", resultSequence)
    expect:
      awaitAcquires(lock.acquire(), lock.acquire(), lock.acquire()) == resultSequence
      awaitRelseases(lock.release(), lock.release(), lock.release()) == resultSequence
  }

  def "should create a lock stub that returns a sequence of different results"() {
    given:
      List<Boolean> acquireResultSequence = [true, false, true]
      List<Boolean> releaseResultSequence = [false, false, true]
    and:
      ReactorDistributedLock lock = sequencedLock("sample-lock", acquireResultSequence, releaseResultSequence)
    expect:
      awaitAcquires(lock.acquire(), lock.acquire(), lock.acquire()) == acquireResultSequence
      awaitRelseases(lock.release(), lock.release(), lock.release()) == releaseResultSequence
  }

  def "should count acquire and release invocations"() {
    given:
      ReactorDistributedLockMock lock = alwaysReleasedLock(lockId)
    expect:
      lock.acquireInvocations() == 0
      lock.releaseInvocations() == 0
    and:
      lock.wasAcquired() == false
      lock.wasReleased() == false

    when:
      lock.acquire().block()
      lock.release().block()
    then:
      lock.acquireInvocations() == 1
      lock.releaseInvocations() == 1
    and:
      lock.wasAcquired() == true
      lock.wasReleased() == true
  }

  private List<Boolean> awaitAcquires(Mono<LockResult>... results) {
    return results
        .collect { it.block() }
        .collect { it.locked }
  }

  private List<Boolean> awaitRelseases(Mono<ReleaseResult>... results) {
    return results
        .collect { it.block() }
        .collect { it.unlocked }
  }
}
