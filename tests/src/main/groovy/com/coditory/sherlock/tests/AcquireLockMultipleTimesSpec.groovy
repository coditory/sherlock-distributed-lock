package com.coditory.sherlock.tests

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.tests.base.LockTypes
import spock.lang.Unroll

import static com.coditory.sherlock.tests.base.LockTypes.OVERRIDING
import static com.coditory.sherlock.tests.base.LockTypes.REENTRANT
import static com.coditory.sherlock.tests.base.LockTypes.allLockTypes

abstract class AcquireLockMultipleTimesSpec extends LocksBaseSpec {
  static List<LockTypes> mayAcquireMultipleTimes = [REENTRANT, OVERRIDING]

  @Unroll
  def "the same instance may acquire lock multiple times - #type"() {
    given:
      DistributedLock lock = createLock(type)
    when:
      boolean firstResult = lock.acquire()
      boolean secondResult = lock.acquire()
    then:
      firstResult == true
      secondResult == true
    where:
      type << mayAcquireMultipleTimes
  }

  @Unroll
  def "the same instance may acquire lock only once - #type"() {
    given:
      DistributedLock lock = createLock(type)
    when:
      boolean firstResult = lock.acquire()
      boolean secondResult = lock.acquire()
    then:
      firstResult == true
      secondResult == false
    where:
      type << allLockTypes() - mayAcquireMultipleTimes
  }

  @Unroll
  def "the same instance may acquire lock multiple times by separate lock objects - #type"() {
    given:
      DistributedLock lock = createLock(type)
      DistributedLock otherObject = createLock(type)
    when:
      boolean firstResult = lock.acquire()
      boolean secondResult = otherObject.acquire()
    then:
      firstResult == true
      secondResult == true
    where:
      type << mayAcquireMultipleTimes
  }

  @Unroll
  def "only one of two different instances may acquire lock - #type"() {
    given:
      DistributedLock lock = createLock(type)
      DistributedLock otherLock = createLock(type)
    when:
      boolean firstResult = lock.acquire()
      boolean secondResult = otherLock.acquire()
    then:
      firstResult == true
    and:
      secondResult == false
    where:
      type << allLockTypes() - mayAcquireMultipleTimes
  }
}
