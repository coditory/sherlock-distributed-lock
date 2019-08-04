package com.coditory.sherlock.reactive

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.tests.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.tests.base.BlockingReactiveSherlockWrapper.blockingReactiveSherlock

trait UsesReactiveInMemorySherlock implements DistributedLocksCreator {
  @Override
  Sherlock createDistributedLocks(String instanceId, Duration duration, Clock clock) {
    ReactiveSherlock reactiveLocks = ReactiveInMemorySherlock.builder()
      .withOwnerId(instanceId)
      .withLockDuration(duration)
      .withClock(clock)
      .build()
    return blockingReactiveSherlock(reactiveLocks)
  }
}
