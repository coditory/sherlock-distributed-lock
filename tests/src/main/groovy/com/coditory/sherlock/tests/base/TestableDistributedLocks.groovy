package com.coditory.sherlock.tests.base

import com.coditory.sherlock.DistributedLock
import groovy.transform.CompileStatic

import java.time.Duration

@CompileStatic
interface TestableDistributedLocks {
  String getOwnerId();

  Duration getDefaultDuration();

  DistributedLock createReentrantLock(String lockId);

  DistributedLock createReentrantLock(String lockId, Duration duration);

  DistributedLock createLock(String lockId);

  DistributedLock createLock(String lockId, Duration duration);

  DistributedLock createOverridingLock(String lockId);

  DistributedLock createOverridingLock(String lockId, Duration duration);
}
