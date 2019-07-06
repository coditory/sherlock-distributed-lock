package com.coditory.distributed.lock.tests.base

import com.coditory.distributed.lock.DistributedLock
import com.coditory.distributed.lock.common.InstanceId

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
