package com.coditory.sherlock.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.DistributedLockBuilder
import com.coditory.sherlock.Sherlock
import groovy.transform.CompileStatic

@CompileStatic
enum LockTypes {
  SINGLE_ENTRANT{
    @Override
    DistributedLockBuilder<DistributedLock> createLock(Sherlock sherlock) {
      return sherlock.createLock()
    }
  },
  REENTRANT{
    @Override
    DistributedLockBuilder<DistributedLock> createLock(Sherlock sherlock) {
      return sherlock.createReentrantLock()
    }
  },
  OVERRIDING{
    @Override
    DistributedLockBuilder<DistributedLock> createLock(Sherlock sherlock) {
      return sherlock.createOverridingLock()
    }
  };

  abstract DistributedLockBuilder<DistributedLock> createLock(Sherlock sherlock);

  static List<LockTypes> allLockTypes() {
    return values().toList()
  }
}
