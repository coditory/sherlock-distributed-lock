package com.coditory.sherlock

import com.coditory.sherlock.tests.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

trait UsesInMemorySherlock implements DistributedLocksCreator {
  @Override
  Sherlock createDistributedLocks(String instanceId, Duration duration, Clock clock) {
    return InMemorySherlock.builder()
      .withOwnerId(instanceId)
      .withLockDuration(duration)
      .withClock(clock)
      .build()
  }
}

