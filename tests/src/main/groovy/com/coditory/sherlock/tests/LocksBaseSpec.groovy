package com.coditory.sherlock.tests

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.tests.base.DistributedLocksCreator
import com.coditory.sherlock.tests.base.LockTypes
import com.coditory.sherlock.tests.base.UpdatableFixedClock
import groovy.transform.CompileStatic
import org.junit.After
import spock.lang.Specification

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.tests.base.UpdatableFixedClock.defaultUpdatableFixedClock

@CompileStatic
abstract class LocksBaseSpec extends Specification implements DistributedLocksCreator {
  static final UpdatableFixedClock fixedClock = defaultUpdatableFixedClock()
  static final Duration defaultLockDuration = Duration.ofMinutes(10)
  static final String sampleOwnerId = "locks-test-instance"
  static final String sampleLockId = "sample-acquire-id"

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
    String instanceId = sampleOwnerId,
    Duration duration = defaultLockDuration) {
    return type.createLock(createSherlock(instanceId, duration), lockId)
  }

  Sherlock createSherlock(String instanceId = sampleOwnerId, Duration duration = defaultLockDuration, Clock clock = fixedClock) {
    return createDistributedLocks(instanceId, duration, clock)
  }
}

