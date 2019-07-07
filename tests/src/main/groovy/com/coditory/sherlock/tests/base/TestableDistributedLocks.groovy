package com.coditory.sherlock.tests.base

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.common.InstanceId

import java.time.Duration

interface TestableDistributedLocks {
  InstanceId getInstanceId();

  Duration getDefaultDuration();

  DistributedLock createReentrantLock(String lockId);

  DistributedLock createReentrantLock(String lockId, Duration duration);

  DistributedLock createLock(String lockId);

  DistributedLock createLock(String lockId, Duration duration);

  DistributedLock createOverridingLock(String lockId);

  DistributedLock createOverridingLock(String lockId, Duration duration);
}
