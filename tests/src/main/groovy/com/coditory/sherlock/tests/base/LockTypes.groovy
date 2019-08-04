package com.coditory.sherlock.tests.base


import com.coditory.sherlock.DistributedLockBuilder
import com.coditory.sherlock.Sherlock
import groovy.transform.CompileStatic

@CompileStatic
enum LockTypes {
  SINGLE_ENTRANT{
    @Override
    DistributedLockBuilder createLock(Sherlock sherlock) {
      return sherlock.createLock()
    }
  },
  REENTRANT{
    @Override
    DistributedLockBuilder createLock(Sherlock sherlock) {
      return sherlock.createReentrantLock()
    }
  },
  OVERRIDING{
    @Override
    DistributedLockBuilder createLock(Sherlock sherlock) {
      return sherlock.createOverridingLock()
    }
  };

  abstract DistributedLockBuilder createLock(Sherlock sherlock);

  static List<LockTypes> allLockTypes() {
    return values().toList()
  }
}
