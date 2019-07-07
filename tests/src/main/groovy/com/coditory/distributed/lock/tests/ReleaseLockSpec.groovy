package com.coditory.distributed.lock.tests

import com.coditory.distributed.lock.DistributedLock
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.distributed.lock.tests.base.LockTypes.OVERRIDING
import static com.coditory.distributed.lock.tests.base.LockTypes.REENTRANT
import static com.coditory.distributed.lock.tests.base.LockTypes.SINGLE_ENTRANT
import static com.coditory.distributed.lock.tests.base.LockTypes.allLockTypes

abstract class ReleaseLockSpec extends LocksBaseSpec {
  String otherInstanceId = "other-instance-id"

  @Unroll
  def "should release a lock after unlocking - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleInstanceId)
      DistributedLock otherLock = createLock(REENTRANT, sampleLockId, otherInstanceId)
    and:
      lock.acquire()

    when:
      boolean unlockResult = lock.release()
    then:
      unlockResult == true

    when:
      boolean lockResult = otherLock.acquire()
    then:
      lockResult == true

    where:
      type << allLockTypes()
  }

  @Unroll
  def "should release a lock after default lock duration - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleInstanceId)
      DistributedLock otherLock = createLock(REENTRANT, sampleLockId, otherInstanceId)
    and:
      lock.acquire()

    when:
      fixedClock.tick(defaultLockDuration)
      boolean lockResult = otherLock.acquire()
    then:
      lockResult == true

    where:
      type << allLockTypes()
  }

  @Unroll
  def "should release a lock after custom lock duration - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleInstanceId)
      DistributedLock otherLock = createLock(REENTRANT, sampleLockId, otherInstanceId)
    and:
      Duration duration = Duration.ofSeconds(5)
      lock.acquire(duration)

    when:
      fixedClock.tick(duration)
      boolean lockResult = otherLock.acquire()
    then:
      lockResult == true

    where:
      type << allLockTypes()
  }

  @Unroll
  def "only the instance that acquired a lock can release it - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleInstanceId)
      DistributedLock otherLock = createLock(type, sampleLockId, otherInstanceId)
    and:
      otherLock.acquire()

    when:
      boolean unlockResult = lock.release()
    then:
      unlockResult == false

    when:
      boolean lockResult = otherLock.release()
    then:
      lockResult == true

    where:
      type << [REENTRANT, SINGLE_ENTRANT]
  }

  def "overriding lock may release a lock acquired by other instance"() {
    given:
      DistributedLock lock = createLock(OVERRIDING, sampleLockId, sampleInstanceId)
      DistributedLock otherLock = createLock(REENTRANT, sampleLockId, otherInstanceId)
    and:
      otherLock.acquire()

    when:
      boolean unlockResult = lock.release()
    then:
      unlockResult == true
  }
}
