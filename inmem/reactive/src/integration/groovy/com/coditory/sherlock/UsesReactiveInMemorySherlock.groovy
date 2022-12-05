package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

import static BlockingReactiveSherlockWrapper.blockingReactiveSherlock
import static com.coditory.sherlock.ReactiveInMemorySherlockBuilder.reactiveInMemorySherlockBuilder

trait UsesReactiveInMemorySherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
        ReactiveSherlock reactiveLocks = reactiveInMemorySherlockBuilder()
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingReactiveSherlock(reactiveLocks)
    }
}
