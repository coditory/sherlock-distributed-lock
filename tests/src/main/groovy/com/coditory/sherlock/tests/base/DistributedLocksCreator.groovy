package com.coditory.sherlock.tests.base

import groovy.transform.CompileStatic

import java.time.Clock
import java.time.Duration

@CompileStatic
interface DistributedLocksCreator {
  TestableDistributedLocks createDistributedLocks(String instanceId, Duration duration, Clock clock);
}
