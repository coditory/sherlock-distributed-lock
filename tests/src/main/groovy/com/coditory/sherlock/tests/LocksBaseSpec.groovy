package com.coditory.sherlock.tests

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.tests.base.TestableDistributedLocks
import com.coditory.sherlock.tests.base.UpdatableFixedClock
import com.coditory.sherlock.tests.base.DistributedLocksCreator
import com.coditory.sherlock.tests.base.LockTypes
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
  static final String sampleInstanceId = "locks-test-instance"
  static final String sampleLockId = "sample-acquire-id"

  @After
  void resetClock() {
    fixedClock.reset()
  }

  DistributedLock createLock(
      LockTypes type,
      String lockId = sampleLockId,
      String instanceId = sampleInstanceId,
      Duration duration = defaultLockDuration) {
    TestableDistributedLocks distributedLocks = distributedLocks(instanceId, duration)
    return type.createLock(distributedLocks, lockId)
  }

  DistributedLock reentrantLock(String lockId = sampleLockId, String instanceId = sampleInstanceId, Duration duration = defaultLockDuration) {
    return distributedLocks(instanceId, duration)
        .createReentrantLock(lockId)
  }

  TestableDistributedLocks distributedLocks(String instanceId = sampleInstanceId, Duration duration = defaultLockDuration, Clock clock = fixedClock) {
    return createDistributedLocks(instanceId, duration, clock)
  }
}

