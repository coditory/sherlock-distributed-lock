package com.coditory.sherlock.base

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.ReactiveSherlock

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.ReactiveInMemorySherlockBuilder.reactiveInMemorySherlockBuilder
import static com.coditory.sherlock.RxSherlock.rxSherlock
import static com.coditory.sherlock.BlockingRxSherlock.blockingRxJavaSherlock

trait UsesRxSherlock implements DistributedLocksCreator {
  @Override
  Sherlock createSherlock(String ownerId, Duration duration, Clock clock) {
    ReactiveSherlock reactiveSherlock = reactiveInMemorySherlockBuilder()
      .withOwnerId(ownerId)
      .withLockDuration(duration)
      .withClock(clock)
      .build()
    return blockingRxJavaSherlock(rxSherlock(reactiveSherlock))
  }
}
