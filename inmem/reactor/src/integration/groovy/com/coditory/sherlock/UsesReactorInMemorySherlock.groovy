package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

import static BlockingReactorSherlockWrapper.blockingReactorSherlock
import static ReactorInMemorySherlockBuilder.reactorInMemorySherlockBuilder

trait UsesReactorInMemorySherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
        ReactorSherlock reactiveLocks = reactorInMemorySherlockBuilder()
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingReactorSherlock(reactiveLocks)
    }
}
