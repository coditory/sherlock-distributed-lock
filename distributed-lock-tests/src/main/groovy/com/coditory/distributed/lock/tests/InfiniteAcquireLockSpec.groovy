package com.coditory.distributed.lock.tests

import com.coditory.distributed.lock.DistributedLock
import spock.lang.Unroll

import static com.coditory.distributed.lock.tests.base.LockTypes.REENTRANT
import static com.coditory.distributed.lock.tests.base.LockTypes.allLockTypes

abstract class InfiniteAcquireLockSpec extends LocksBaseSpec {
  String otherInstanceId = "other-instance-id"

  @Unroll
  def "infinite lock should stay blocked even after default lock duration - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleInstanceId)
      DistributedLock otherLock = createLock(REENTRANT, sampleLockId, otherInstanceId)
    and:
      lock.lockInfinitely()

    when:
      fixedClock.tick(defaultLockDuration)
      boolean lockResult = otherLock.lock()
    then:
      lockResult == false

    where:
      type << allLockTypes()
  }
}
