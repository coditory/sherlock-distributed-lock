package com.coditory.xlock.mongo

import com.coditory.xlock.api.CrossServiceLock
import com.coditory.xlock.api.CrossServiceLockOperations
import com.coditory.xlock.api.AcquireLockResult

import java.time.Duration

class AcquireLockIntgSpec extends MongoIntgSpec {
  CrossServiceLockOperations lock = createLockOperations("sample-lock")

  def "the same lock instance may acquire lock multiple times"() {
    when:
      AcquireLockResult firstResult = lock.lock()
      AcquireLockResult secondResult = lock.lock()
    then:
      firstResult.lockGranted
    and:
      secondResult.lockGranted
  }

  def "only one of lock instances should acquire lock"() {
    when:
      AcquireLockResult firstResult = lock.lock()
      AcquireLockResult secondResult = createLock("sample-lock").lock()
      AcquireLockResult thirdResult = lock.newInstance().lock()
    then:
      firstResult.lockGranted
    and:
      !secondResult.lockGranted
    and:
      !thirdResult.lockGranted
  }

  def "two locks with different lock ids should not block each other"() {
    given:
      CrossServiceLock otherLock = createLock("other-lock")
    when:
      AcquireLockResult firstResult = lock.lock()
      AcquireLockResult secondResult = otherLock.lock()
    then:
      firstResult.lockGranted
    and:
      secondResult.lockGranted
  }

  def "should release a lock after unlocking"() {
    given:
      lock.lock().unlock()
    when:
      AcquireLockResult lockResult = lock.newInstance().lock()
    then:
      lockResult.lockGranted
  }

  def "should release a lock after default lock duration"() {
    given:
      lock.lock()
    when:
      fixedClock.tick(lock.duration)
      AcquireLockResult lockResult = lock.newInstance().lock()
    then:
      lockResult.lockGranted
  }

  def "should release a lock after custom lock duration"() {
    given:
      Duration duration = Duration.ofSeconds(5)
      lock.lock(duration)
    when:
      fixedClock.tick(lock.duration)
      AcquireLockResult lockResult = lock.newInstance().lock()
    then:
      lockResult.lockGranted
  }

  def "infinite lock should stay blocked after default lock duration"() {
    given:
      lock.lockInfinitely()
    when:
      fixedClock.tick(lock.duration)
      AcquireLockResult lockResult = lock.newInstance().lock()
    then:
      !lockResult.lockGranted
  }
}
