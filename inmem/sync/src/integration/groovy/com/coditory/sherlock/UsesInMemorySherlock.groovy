package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

trait UsesInMemorySherlock implements DistributedLocksCreator {
  @Override
  Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
    return InMemorySherlock.builder()
      .withOwnerId(instanceId)
      .withLockDuration(duration)
      .withClock(clock)
      .build()
  }
}

