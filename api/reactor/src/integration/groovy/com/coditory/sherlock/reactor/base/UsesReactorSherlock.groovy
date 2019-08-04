package com.coditory.sherlock.reactor.base

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.reactive.ReactiveInMemorySherlock
import com.coditory.sherlock.reactive.ReactiveSherlock
import com.coditory.sherlock.tests.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.reactor.ReactorSherlock.toReactorSherlock
import static BlockingReactorSherlockWrapper.blockingReactorSherlock

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
