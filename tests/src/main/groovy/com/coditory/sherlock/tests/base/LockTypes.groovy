package com.coditory.sherlock.tests.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.Sherlock
import groovy.transform.CompileStatic

@CompileStatic
enum LockTypes {
  SINGLE_ENTRANT{
    @Override
    DistributedLock createLock(Sherlock sherlock, String lockId) {
      return sherlock
        .createLock(lockId)
    }
  },
  REENTRANT{
    @Override
    DistributedLock createLock(Sherlock sherlock, String lockId) {
      return sherlock
        .createReentrantLock(lockId)
    }
  },
  OVERRIDING{
    @Override
    DistributedLock createLock(Sherlock sherlock, String lockId) {
      return sherlock
        .createOverridingLock(lockId)
    }
  };

  abstract DistributedLock createLock(Sherlock sherlock, String lockId);

  static List<LockTypes> allLockTypes() {
    return values().toList()
  }
}
