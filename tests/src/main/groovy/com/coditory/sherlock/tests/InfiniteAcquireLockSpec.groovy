package com.coditory.sherlock.tests

import com.coditory.sherlock.DistributedLock
import spock.lang.Unroll

import static com.coditory.sherlock.tests.base.LockTypes.REENTRANT
import static com.coditory.sherlock.tests.base.LockTypes.allLockTypes

abstract class InfiniteAcquireLockSpec extends LocksBaseSpec {
  String otherInstanceId = "other-instance-id"

  @Unroll
  def "infinite lock should stay blocked even after default lock duration - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleInstanceId)
      DistributedLock otherLock = createLock(REENTRANT, sampleLockId, otherInstanceId)
    and:
      lock.acquireForever()

    when:
      fixedClock.tick(defaultLockDuration)
      boolean lockResult = otherLock.acquire()
    then:
      lockResult == false

    where:
      type << allLockTypes()
  }
}
