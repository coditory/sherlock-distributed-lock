package com.coditory.sherlock.rxjava.base

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.reactive.ReactiveInMemorySherlock
import com.coditory.sherlock.reactive.ReactiveSherlock
import com.coditory.sherlock.tests.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.rxjava.RxJavaSherlock.toRxSherlock
import static BlockingRxJavaSherlock.blockingRxJavaSherlock

trait UsesRxJavaSherlock implements DistributedLocksCreator {
  @Override
  Sherlock createDistributedLocks(String ownerId, Duration duration, Clock clock) {
    ReactiveSherlock reactiveSherlock = ReactiveInMemorySherlock.builder()
      .withOwnerId(ownerId)
      .withLockDuration(duration)
      .withClock(clock)
      .build()
    return blockingRxJavaSherlock(toRxSherlock(reactiveSherlock))
  }
}
