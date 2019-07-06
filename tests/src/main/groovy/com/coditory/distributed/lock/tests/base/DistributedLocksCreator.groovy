package com.coditory.distributed.lock.tests.base


import java.time.Clock
import java.time.Duration

interface DistributedLocksCreator {
  TestableDistributedLocks createDistributedLocks(String instanceId, Duration duration, Clock clock);
}
