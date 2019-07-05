package com.coditory.distributed.lock.mongo

import com.coditory.distributed.lock.DistributedLock
import com.coditory.distributed.lock.mongo.base.LockTypes
import spock.lang.Unroll

import static com.coditory.distributed.lock.mongo.base.LockTypes.OVERRIDING
import static com.coditory.distributed.lock.mongo.base.LockTypes.REENTRANT
import static com.coditory.distributed.lock.mongo.base.LockTypes.allLockTypes

class AcquireLockMultipleTimesIntgSpec extends MongoLocksIntgSpec {
  static String otherInstanceId = "other-instance-id"
  static List<LockTypes> mayAcquireMultipleTimes = [REENTRANT, OVERRIDING]


  @Unroll
  def "the same instance may acquire lock multiple times - #type"() {
    given:
      DistributedLock lock = createLock(type)
    when:
      boolean firstResult = lock.lock()
      boolean secondResult = lock.lock()
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
      boolean firstResult = lock.lock()
      boolean secondResult = lock.lock()
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
      boolean firstResult = lock.lock()
      boolean secondResult = otherObject.lock()
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
      boolean firstResult = lock.lock()
      boolean secondResult = otherLock.lock()
    then:
      firstResult == true
    and:
      secondResult == false
    where:
      type << allLockTypes() - mayAcquireMultipleTimes
  }
}
