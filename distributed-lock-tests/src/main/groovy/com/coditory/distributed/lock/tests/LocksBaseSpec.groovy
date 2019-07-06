package com.coditory.distributed.lock.tests

import com.coditory.distributed.lock.DistributedLock
import com.coditory.distributed.lock.DistributedLockDriver
import com.coditory.distributed.lock.DistributedLocks
import com.coditory.distributed.lock.tests.base.DistributedLockDriverProvider
import com.coditory.distributed.lock.tests.base.LockTypes
import com.coditory.distributed.lock.tests.base.UpdatableFixedClock
import groovy.transform.CompileStatic
import org.junit.After
import spock.lang.Specification

import java.time.Duration

import static com.coditory.distributed.lock.tests.base.UpdatableFixedClock.defaultUpdatableFixedClock

@CompileStatic
abstract class LocksBaseSpec extends Specification implements DistributedLockDriverProvider {
  static final UpdatableFixedClock fixedClock = defaultUpdatableFixedClock()
  static final Duration defaultLockDuration = Duration.ofMinutes(10)
  static final String sampleInstanceId = "locks-test-instance"
  static final String sampleLockId = "sample-lock-id"
  DistributedLockDriver driver = getDriver(fixedClock)

  @After
  void resetClock() {
    fixedClock.reset()
  }

  DistributedLock createLock(
      LockTypes type,
      String lockId = sampleLockId,
      String instanceId = sampleInstanceId,
      Duration duration = defaultLockDuration) {
    return type.createLock(
        driver,
        lockId,
        instanceId,
        duration
    )
  }

  DistributedLock reentrantLock(String lockId = sampleLockId, String instanceId = sampleInstanceId, Duration duration = defaultLockDuration) {
    return distributedLocks(instanceId, duration)
        .createReentrantLock(lockId)
  }

  DistributedLocks distributedLocks(String instanceId = sampleInstanceId, Duration duration = defaultLockDuration) {
    return DistributedLocks.builder(driver)
        .withDefaultLockDurationd(duration)
        .withServiceInstanceId(instanceId)
        .build()
  }
}

