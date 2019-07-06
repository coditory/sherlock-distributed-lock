package com.coditory.distributed.lock.tests.base

import com.coditory.distributed.lock.DistributedLockDriver

import java.time.Clock

interface DistributedLockDriverProvider {

  DistributedLockDriver getDriver(Clock clock);

}
