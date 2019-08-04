package com.coditory.sherlock.tests

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.tests.base.DistributedLocksCreator
import com.coditory.sherlock.tests.base.LockTypes
import com.coditory.sherlock.tests.base.UpdatableFixedClock
import org.junit.After
import org.junit.Before
import spock.lang.Specification

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.tests.base.UpdatableFixedClock.defaultUpdatableFixedClock

abstract class LocksBaseSpec extends Specification implements DistributedLocksCreator {
  static final UpdatableFixedClock fixedClock = defaultUpdatableFixedClock()
  static final Duration defaultLockDuration = Duration.ofMinutes(10)
  static final String sampleOwnerId = "locks-test-instance"
  static final String sampleLockId = "sample-acquire-id"
  Sherlock sherlock

  @Before
  void setupSherlock() {
    sherlock = createSherlock()
  }

  @After
  void resetClock() {
    fixedClock.reset()
  }

  @After
  void releaseAllLocks() {
    createSherlock().forceReleaseAllLocks()
  }

  DistributedLock createLock(
    LockTypes type,
    String lockId = sampleLockId,
    String ownerId = sampleOwnerId,
    Duration duration = defaultLockDuration) {
    return type.createLock(sherlock)
      .withLockId(lockId)
      .withOwnerId(ownerId)
      .withLockDuration(duration)
      .build()
  }

  Sherlock createSherlock(String ownerId = sampleOwnerId, Duration duration = defaultLockDuration) {
    return createSherlock(ownerId, duration, fixedClock as Clock)
  }
}

