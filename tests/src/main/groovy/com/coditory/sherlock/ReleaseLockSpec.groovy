package com.coditory.sherlock


import com.coditory.sherlock.base.LockAssertions
import spock.lang.Unroll

import java.time.Duration

import static UuidGenerator.uuid
import static com.coditory.sherlock.base.LockTypes.OVERRIDING
import static com.coditory.sherlock.base.LockTypes.REENTRANT
import static com.coditory.sherlock.base.LockTypes.SINGLE_ENTRANT
import static com.coditory.sherlock.base.LockTypes.allLockTypes

abstract class ReleaseLockSpec extends LocksBaseSpec implements LockAssertions {
  String otherOwnerId = "other-instance-id"

  @Unroll
  def "should release a previously acquired lock - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleOwnerId)
    and:
      lock.acquire()

    when:
      boolean unlockResult = lock.release()
    then:
      unlockResult == true
    and:
      assertReleased(lock.id)

    where:
      type << allLockTypes()
  }

  @Unroll
  def "should not release a lock that was not acquired - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleOwnerId)

    when:
      boolean unlockResult = lock.release()
    then:
      unlockResult == false
    and:
      assertReleased(lock.id)

    where:
      type << allLockTypes()
  }

  @Unroll
  def "should automatically release a lock after default lock duration - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleOwnerId)
    and:
      lock.acquire()

    when:
      fixedClock.tick(defaultLockDuration)
    then:
      assertReleased(lock.id)

    where:
      type << allLockTypes()
  }

  @Unroll
  def "should automatically release a lock after custom lock duration - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleOwnerId)
    and:
      Duration duration = Duration.ofSeconds(5)
      lock.acquire(duration)

    when:
      fixedClock.tick(duration)
    then:
      assertReleased(lock.id)

    where:
      type << allLockTypes()
  }

  @Unroll
  def "only the instance that acquired a lock can release it - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleOwnerId)
      DistributedLock otherLock = createLock(type, sampleLockId, otherOwnerId)
    and:
      otherLock.acquire()

    when:
      boolean unlockResult = lock.release()
    then:
      unlockResult == false
      assertAcquired(lock.id)

    when:
      boolean lockResult = otherLock.release()
    then:
      lockResult == true
      assertReleased(lock.id)

    where:
      type << [REENTRANT, SINGLE_ENTRANT]
  }

  def "overriding lock may release a lock acquired by other owner"() {
    given:
      DistributedLock overridingLock = createLock(OVERRIDING, sampleLockId, sampleOwnerId)
      DistributedLock otherLock = createLock(REENTRANT, sampleLockId, otherOwnerId)
    and:
      otherLock.acquire()

    when:
      boolean unlockResult = overridingLock.release()
    then:
      unlockResult == true
      assertReleased(otherLock.id)
  }

  @Unroll
  def "should return false for releasing an expired lock - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleOwnerId)
      lock.acquire()
      fixedClock.tick(defaultLockDuration)

    when:
      boolean unlockResult = lock.release()
    then:
      unlockResult == false

    where:
      type << allLockTypes()
  }

  def "should force release all locks"() {
    given:
      DistributedLock overridingLock = createLock(OVERRIDING, uuid(), uuid())
      DistributedLock reentrantLock = createLock(REENTRANT, uuid(), uuid())
      DistributedLock singleEntrantLock = createLock(SINGLE_ENTRANT, uuid(), uuid())
    and:
      overridingLock.acquire()
      reentrantLock.acquire()
      singleEntrantLock.acquire()
    when:
      sherlock.forceReleaseAllLocks()
    then:
      assertReleased(overridingLock.id)
      assertReleased(reentrantLock.id)
      assertReleased(singleEntrantLock.id)
  }

  def "should force release single lock"() {
    given:
      DistributedLock overridingLock = createLock(OVERRIDING, uuid(), uuid())
      DistributedLock reentrantLock = createLock(REENTRANT, uuid(), uuid())
      DistributedLock singleEntrantLock = createLock(SINGLE_ENTRANT, uuid(), uuid())
    and:
      overridingLock.acquire()
      reentrantLock.acquire()
      singleEntrantLock.acquire()
    when:
      sherlock.forceReleaseLock(overridingLock.id)
    then:
      assertReleased(overridingLock.id)
    and:
      assertAcquired(reentrantLock.id)
      assertAcquired(singleEntrantLock.id)
  }
}
