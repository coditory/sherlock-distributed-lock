package com.coditory.distributed.lock.mongo.base

import com.coditory.distributed.lock.DistributedLock
import com.coditory.distributed.lock.DistributedLocks
import com.coditory.distributed.lock.common.driver.DistributedLockDriver
import groovy.transform.CompileStatic

import java.time.Duration

@CompileStatic
enum LockTypes {
  REENTRANT{
    @Override
    DistributedLock createLock(DistributedLockDriver driver, String lockId, String instanceId, Duration duration) {
      return distributedLocks(driver, instanceId, duration)
          .createReentrantLock(lockId)
    }
  },
  SINGLE_ENTRANT{
    @Override
    DistributedLock createLock(DistributedLockDriver driver, String lockId, String instanceId, Duration duration) {
      return distributedLocks(driver, instanceId, duration)
          .createLock(lockId)
    }
  },
  OVERRIDING{
    @Override
    DistributedLock createLock(DistributedLockDriver driver, String lockId, String instanceId, Duration duration) {
      return distributedLocks(driver, instanceId, duration)
          .createOverridingLock(lockId)
    }
  };

  abstract DistributedLock createLock(
      DistributedLockDriver driver,
      String lockId,
      String instanceId,
      Duration duration);

  DistributedLocks distributedLocks(DistributedLockDriver driver, String instanceId, Duration duration) {
    return DistributedLocks.builder(driver)
        .withDefaultLockDurationd(duration)
        .withServiceInstanceId(instanceId)
        .build()
  }

  static List<LockTypes> allLockTypes() {
    return values().toList()
  }
}
