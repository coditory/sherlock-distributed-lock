package com.coditory.sherlock

import com.coditory.sherlock.base.LockAssertions
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.sherlock.base.LockTypes.OVERRIDING
import static com.coditory.sherlock.base.LockTypes.REENTRANT
import static com.coditory.sherlock.base.LockTypes.SINGLE_ENTRANT
import static com.coditory.sherlock.base.LockTypes.allLockTypes

abstract class AcquireLockSpec extends LocksBaseSpec implements LockAssertions {
  String lockId = "acquire-id"
  String instanceId = "instance-id"
  String otherInstanceId = "other-instance-id"

  @Unroll
  def "only one instance may acquire a lock - #type"() {
    given:
      DistributedLock lock = createLock(type, lockId, instanceId)
      DistributedLock otherLock = createLock(type, lockId, otherInstanceId)
    when:
      boolean firstResult = lock.acquire()
      boolean secondResult = otherLock.acquire()
    then:
      firstResult == true
      secondResult == false
    and:
      assertAcquired(lock)
      assertLocked(otherLock)
    where:
      type << [REENTRANT, SINGLE_ENTRANT]
  }

  def "overriding lock may acquire not released lock"() {
    given:
      DistributedLock lock = createLock(REENTRANT, lockId, instanceId)
      DistributedLock overridingLock = createLock(OVERRIDING, lockId, otherInstanceId)
    when:
      boolean firstResult = lock.acquire()
      boolean secondResult = overridingLock.acquire()
    then:
      firstResult == true
      secondResult == true
    and:
      assertLocked(lock)
      assertAcquired(overridingLock)
  }

  @Unroll
  def "two locks with different ids should not block each other - #type"() {
    given:
      DistributedLock lock = createLock(type, lockId, instanceId)
      DistributedLock otherLock = createLock(REENTRANT, "other-acquire", otherInstanceId)
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
      type << allLockTypes()
  }

  @Unroll
  def "should throw error when duration uses nanoseconds - #type"() {
    given:
      DistributedLock lock = createLock(type, lockId, instanceId)
    and:
      String truncationExceptionPrefix = "Expected lock duration truncated to millis"

    when:
      lock.acquire(Duration.ofNanos(1))
    then:
      IllegalArgumentException exception = thrown(IllegalArgumentException)
      exception.message.startsWith(truncationExceptionPrefix)
    and:
      assertReleased(lock)

    when:
      lock.acquire(Duration.ofSeconds(1).plusNanos(1))
    then:
      exception = thrown(IllegalArgumentException)
      exception.message.startsWith(truncationExceptionPrefix)
    and:
      assertReleased(lock)

    where:
      type << allLockTypes()
  }

  @Unroll
  def "more recent lock operation should overwrite previous one - #type"() {
    given:
      DistributedLock lock = createLock(type, lockId, instanceId)
      Duration duration = Duration.ofHours(1)

    when:
      lock.acquire(duration)
    then:
      assertAcquiredFor(lock, duration)

    when:
      lock.acquireForever()
    then:
      assertAcquiredForever(lock)

    when:
      lock.acquire()
    then:
      assertAcquiredFor(lock, defaultLockDuration)

    where:
      type << [REENTRANT, OVERRIDING]
  }

  @Unroll
  def "should acquire an expired lock - #type"() {
    given:
      DistributedLock lock = createLock(type, lockId, instanceId)
      DistributedLock otherLock = createLock(type, lockId, otherInstanceId)
    and:
      lock.acquire()
      fixedClock.tick(defaultLockDuration)

    when:
      boolean result = otherLock.acquire()

    then:
      result == true
      assertAcquired(otherLock)

    where:
      type << [REENTRANT, SINGLE_ENTRANT]
  }
}
