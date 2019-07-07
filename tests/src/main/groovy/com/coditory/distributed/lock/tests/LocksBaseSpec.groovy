package com.coditory.distributed.lock.tests

import com.coditory.distributed.lock.DistributedLock
import com.coditory.distributed.lock.tests.base.DistributedLocksCreator
import com.coditory.distributed.lock.tests.base.LockTypes
import com.coditory.distributed.lock.tests.base.TestableDistributedLocks
import com.coditory.distributed.lock.tests.base.UpdatableFixedClock
import groovy.transform.CompileStatic
import org.junit.After
import spock.lang.Specification

import java.time.Clock
import java.time.Duration

import static com.coditory.distributed.lock.tests.base.UpdatableFixedClock.defaultUpdatableFixedClock

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

