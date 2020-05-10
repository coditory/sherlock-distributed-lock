package com.coditory.sherlock.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.LockState
import com.coditory.sherlock.LocksBaseSpec
import groovy.transform.CompileStatic
import groovy.transform.SelfType

import java.time.Duration
import java.time.Instant

import static LockTypes.SINGLE_ENTRANT

@CompileStatic
@SelfType(LocksBaseSpec)
trait LockAssertions {
  boolean assertAcquiredForever(DistributedLock lock) {
    return assertAcquiredState(lock) &&
        assertThatCanNotBeAcquiredAfterLongTime(lock.id)
  }

  boolean assertAcquiredFor(DistributedLock lock, Duration duration) {
    return assertAcquiredState(lock) &&
        assertThatCanBeAcquiredAfter(lock.id, duration)
  }

  boolean assertAcquired(DistributedLock lock) {
    return assertAcquiredState(lock) &&
        assertThatCanNotBeAcquired(lock.id)
  }

  boolean assertLockedForever(DistributedLock lock) {
    return assertLockedState(lock) &&
        assertThatCanNotBeAcquiredAfterLongTime(lock.id)
  }

  boolean assertLockedForever(String lockId) {
    return assertLockedState(createLockWithRandomOwner(lockId)) &&
        assertThatCanNotBeAcquiredAfterLongTime(lockId)
  }

  boolean assertLockedFor(DistributedLock lock, Duration duration) {
    return assertLockedState(lock) &&
        assertThatCanBeAcquiredAfter(lock.id, duration)
  }

  boolean assertLockedFor(String lockId, Duration duration) {
    return assertLockedState(createLockWithRandomOwner(lockId)) &&
        assertThatCanBeAcquiredAfter(lockId, duration)
  }

  boolean assertLocked(String lockId) {
    return assertLocked(createLockWithRandomOwner(lockId))
  }

  boolean assertLocked(DistributedLock lock) {
    return assertLockedState(lock) &&
        assertThatCanNotBeAcquired(lock.id)
  }

  boolean assertReleased(String lockId) {
    return assertReleasedState(createLockWithRandomOwner(lockId))
  }

  boolean assertReleased(DistributedLock lock) {
    return assertReleasedState(lock) &&
        assertThatCanBeAcquired(lock.id)
  }

  private boolean assertThatCanNotBeAcquiredAfterLongTime(String lockId, Duration duration = Duration.ofDays(100)) {
    DistributedLock otherLock = createLockWithRandomOwner(lockId)
    assert otherLock.acquire() == false
    Instant backup = fixedClock.instant()
    fixedClock.tick(duration)
    assert otherLock.acquire() == false
    fixedClock.setup(backup)
    return true
  }

  private boolean assertThatCanBeAcquiredAfter(String lockId, Duration duration) {
    DistributedLock otherLock = createLockWithRandomOwner(lockId)
    assert otherLock.acquire() == false
    Instant backup = fixedClock.instant()
    fixedClock.tick(duration - Duration.ofMillis(1))
    assert otherLock.acquire() == false
    fixedClock.tick(Duration.ofMillis(1))
    assert otherLock.acquire() == true
    otherLock.release()
    fixedClock.setup(backup)
    return true
  }

  private boolean assertAcquiredState(DistributedLock lock) {
    assert lock.getState() == LockState.ACQUIRED
    assert lock.isAcquired() == true
    assert lock.isLocked() == true
    assert lock.isUnlocked() == false
    return true
  }

  private boolean assertLockedState(DistributedLock lock) {
    assert lock.getState() == LockState.LOCKED
    assert lock.isAcquired() == false
    assert lock.isLocked() == true
    assert lock.isUnlocked() == false
    return true
  }

  private boolean assertReleasedState(DistributedLock lock) {
    assert lock.getState() == LockState.UNLOCKED
    assert lock.isAcquired() == false
    assert lock.isLocked() == false
    assert lock.isUnlocked() == true
    return true
  }

  private boolean assertThatCanBeAcquired(String lockId) {
    DistributedLock otherLock = createLockWithRandomOwner(lockId)
    assert otherLock.acquire() == true
    otherLock.release()
    return true
  }

  private boolean assertThatCanNotBeAcquired(String lockId) {
    DistributedLock otherLock = createLockWithRandomOwner(lockId)
    assert otherLock.acquire() == false
    return true
  }

  private DistributedLock createLockWithRandomOwner(String lockId) {
    return createLock(SINGLE_ENTRANT, lockId, UUID.randomUUID().toString())
  }
}
