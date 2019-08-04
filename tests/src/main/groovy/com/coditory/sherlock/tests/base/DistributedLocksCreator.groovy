package com.coditory.sherlock.tests.base

import com.coditory.sherlock.Sherlock
import groovy.transform.CompileStatic

import java.time.Clock
import java.time.Duration

@CompileStatic
interface DistributedLocksCreator {
  Sherlock createDistributedLocks(String instanceId, Duration duration, Clock clock);
}
