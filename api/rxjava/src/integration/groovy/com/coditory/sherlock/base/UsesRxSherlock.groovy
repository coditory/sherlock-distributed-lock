package com.coditory.sherlock.base

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.ReactiveInMemorySherlock
import com.coditory.sherlock.ReactiveSherlock

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.RxSherlock.toRxSherlock
import static com.coditory.sherlock.BlockingRxSherlock.blockingRxJavaSherlock

trait UsesRxSherlock implements DistributedLocksCreator {
  @Override
  Sherlock createSherlock(String ownerId, Duration duration, Clock clock) {
    ReactiveSherlock reactiveSherlock = ReactiveInMemorySherlock.builder()
      .withOwnerId(ownerId)
      .withLockDuration(duration)
      .withClock(clock)
      .build()
    return blockingRxJavaSherlock(toRxSherlock(reactiveSherlock))
  }
}
