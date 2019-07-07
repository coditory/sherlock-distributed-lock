package com.coditory.sherlock.tests.base

import com.coditory.sherlock.DistributedLock
import groovy.transform.CompileStatic

@CompileStatic
enum LockTypes {
  REENTRANT{
    @Override
    DistributedLock createLock(TestableDistributedLocks distributedLocks, String lockId) {
      return distributedLocks
          .createReentrantLock(lockId)
    }
  },
  SINGLE_ENTRANT{
    @Override
    DistributedLock createLock(TestableDistributedLocks distributedLocks, String lockId) {
      return distributedLocks
          .createLock(lockId)
    }
  },
  OVERRIDING{
    @Override
    DistributedLock createLock(TestableDistributedLocks distributedLocks, String lockId) {
      return distributedLocks
          .createOverridingLock(lockId)
    }
  };

  abstract DistributedLock createLock(TestableDistributedLocks distributedLocks, String lockId);

  static List<LockTypes> allLockTypes() {
    return values().toList()
  }
}
