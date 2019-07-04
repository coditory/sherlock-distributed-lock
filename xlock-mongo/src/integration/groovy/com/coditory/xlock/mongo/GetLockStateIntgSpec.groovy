package com.coditory.xlock.mongo

import com.coditory.xlock.api.CrossServiceLockOperations
import com.coditory.xlock.common.LockId
import com.coditory.xlock.common.LockState

class GetLockStateIntgSpec extends MongoIntgSpec {
  LockId lockId = LockId.of("some-lock")
  CrossServiceLockOperations lockOperations = lockFactory.createLockOperations(lockId)

  def "should retrieve state of acquired lock"() {
    given:
      lockOperations.lock()
    when:
      LockState lockState = lockOperations.getLockState().get()
    then:
      lockState.lockId == lockId
      lockState.serviceInstanceId == lockFactory.serviceInstanceId
      lockState.lockInstanceId != null
      lockState.acquiredAt == fixedClock.instant()
      lockState.releaseAt.get() == fixedClock.instant() + lockFactory.defaultDuration
  }

  def "should not retrieve state of never acquired lock"() {
    when:
      Optional<LockState> lockState = lockOperations.getLockState()
    then:
      lockState.isEmpty()
  }

  def "should not retrieve state of manually released lock"() {
    given:
      lockOperations.lock()
      lockOperations.forceUnlock()
    when:
      Optional<LockState> lockState = lockOperations.getLockState()
    then:
      lockState.isEmpty()
  }
}
