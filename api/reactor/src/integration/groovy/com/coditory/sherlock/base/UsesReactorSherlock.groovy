package com.coditory.sherlock.base

import com.coditory.sherlock.ReactiveInMemorySherlock
import com.coditory.sherlock.ReactiveSherlock
import com.coditory.sherlock.Sherlock

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingReactorSherlockWrapper.blockingReactorSherlock
import static com.coditory.sherlock.ReactorSherlock.toReactorSherlock

trait UsesReactorSherlock implements DistributedLocksCreator {
  @Override
  Sherlock createSherlock(String ownerId, Duration duration, Clock clock) {
    ReactiveSherlock reactiveSherlock = ReactiveInMemorySherlock.builder()
      .withOwnerId(ownerId)
      .withLockDuration(duration)
      .withClock(clock)
      .build()
    return blockingReactorSherlock(toReactorSherlock(reactiveSherlock))
  }
}
