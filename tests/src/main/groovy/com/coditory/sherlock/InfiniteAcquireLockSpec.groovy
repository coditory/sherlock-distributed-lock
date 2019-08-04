package com.coditory.sherlock


import com.coditory.sherlock.base.LockAssertions
import spock.lang.Unroll

import static com.coditory.sherlock.base.LockTypes.allLockTypes

abstract class InfiniteAcquireLockSpec extends LocksBaseSpec implements LockAssertions {
  @Unroll
  def "infinite lock should stay acquired even when default lock duration passes - #type"() {
    given:
      DistributedLock lock = createLock(type, sampleLockId, sampleOwnerId)
    and:
      lock.acquireForever()

    when:
      fixedClock.tick(defaultLockDuration)
    then:
      assertAcquired(lock.id)

    where:
      type << allLockTypes()
  }
}
