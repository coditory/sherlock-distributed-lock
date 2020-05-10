package com.coditory.sherlock

import com.coditory.sherlock.base.LockAssertions
import com.coditory.sherlock.base.LockTypes
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.sherlock.base.LockTypes.OVERRIDING
import static com.coditory.sherlock.base.LockTypes.REENTRANT
import static com.coditory.sherlock.base.LockTypes.allLockTypes

abstract class AcquireLockMultipleTimesSpec extends LocksBaseSpec
    implements LockAssertions {
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
    and:
      assertAcquired(lock)
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
    and:
      assertAcquired(lock)
    where:
      type << allLockTypes() - mayAcquireMultipleTimes
  }

  @Unroll
  def "two instances may acquire lock multiple times - #type"() {
    given:
      DistributedLock lock = createLock(type)
      DistributedLock otherLock = createLock(type)
    when:
      boolean firstResult = lock.acquire()
      boolean secondResult = otherLock.acquire()
    then:
      firstResult == true
      secondResult == true
    and:
      assertAcquired(lock)
      assertAcquired(otherLock)
    where:
      type << mayAcquireMultipleTimes
  }

  @Unroll
  def "only one instance may acquire lock in the same time - #type"() {
    given:
      DistributedLock lock = createLock(type)
      DistributedLock otherLock = createLock(type)
    when:
      boolean firstResult = lock.acquire()
      boolean secondResult = otherLock.acquire()
    then:
      firstResult == true
      secondResult == false
    and:
      assertAcquired(lock)
      assertAcquired(otherLock)
    where:
      type << allLockTypes() - mayAcquireMultipleTimes
  }

  def "should prolong lock duration when reentrant lock is acquired multiple times"() {
    given:
      DistributedLock lock = createLock(REENTRANT)
    and:
      lock.acquire(Duration.ofHours(1))
      fixedClock.tick(Duration.ofMinutes(30))
    when:
      lock.acquire(Duration.ofHours(1))
    and:
      fixedClock.tick(Duration.ofMinutes(45))
    then:
      lock.release() == true
  }
}
