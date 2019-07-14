package com.coditory.sherlock.rxjava

import com.coditory.sherlock.reactive.connector.LockResult
import com.coditory.sherlock.reactive.connector.ReleaseResult
import io.reactivex.Single
import spock.lang.Specification

import static com.coditory.sherlock.rxjava.base.DistributedLockAssertions.assertAlwaysClosedLock
import static com.coditory.sherlock.rxjava.base.DistributedLockAssertions.assertAlwaysOpenedLock
import static com.coditory.sherlock.rxjava.test.RxJavaDistributedLockMock.alwaysAcquiredLock
import static com.coditory.sherlock.rxjava.test.RxJavaDistributedLockMock.alwaysReleasedLock
import static com.coditory.sherlock.rxjava.test.RxJavaDistributedLockMock.sequencedLock

class RxJavaDistributedLockMockSpec extends Specification {
  String lockId = "sample-lock"

  def "should create always open lock that returns always success"() {
    given:
      RxJavaDistributedLock lock = alwaysReleasedLock(lockId)
    expect:
      assertAlwaysOpenedLock(lock, lockId)
  }

  def "should create always closed lock that returns always failure"() {
    given:
      RxJavaDistributedLock lock = alwaysAcquiredLock("sample-lock")
    expect:
      assertAlwaysClosedLock(lock, lockId)
  }

  def "should create a lock stub that returns a sequence of results"() {
    given:
      List<Boolean> resultSequence = [true, false, true]
    and:
      RxJavaDistributedLock lock = sequencedLock("sample-lock", resultSequence)
    expect:
      awaitAcquires(lock.acquire(), lock.acquire(), lock.acquire()) == resultSequence
      awaitReleases(lock.release(), lock.release(), lock.release()) == resultSequence
  }

  def "should create a lock stub that returns a sequence of different results"() {
    given:
      List<Boolean> acquireResultSequence = [true, false, true]
      List<Boolean> releaseResultSequence = [false, false, true]
    and:
      RxJavaDistributedLock lock = sequencedLock("sample-lock", acquireResultSequence, releaseResultSequence)
    expect:
      awaitAcquires(lock.acquire(), lock.acquire(), lock.acquire()) == acquireResultSequence
      awaitReleases(lock.release(), lock.release(), lock.release()) == releaseResultSequence
  }

  def "should count acquire and release invocations"() {
    given:
      RxJavaDistributedLock lock = alwaysReleasedLock(lockId)
    expect:
      lock.acquireInvocations() == 0
      lock.releaseInvocations() == 0
    and:
      lock.wasAcquired() == false
      lock.wasReleased() == false

    when:
      lock.acquire().blockingGet()
      lock.release().blockingGet()
    then:
      lock.acquireInvocations() == 1
      lock.releaseInvocations() == 1
    and:
      lock.wasAcquired() == true
      lock.wasReleased() == true
  }

  private List<Boolean> awaitAcquires(Single<LockResult>... results) {
    return results
        .collect { it.blockingGet() }
        .collect { it.locked }
  }

  private List<Boolean> awaitReleases(Single<ReleaseResult>... results) {
    return results
        .collect { it.blockingGet() }
        .collect { it.unlocked }
  }
}
