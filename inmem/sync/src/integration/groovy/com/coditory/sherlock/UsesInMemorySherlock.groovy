package com.coditory.sherlock


import com.coditory.sherlock.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.InMemorySherlockBuilder.inMemorySherlockBuilder

trait UsesInMemorySherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
        return inMemorySherlockBuilder()
            .withOwnerId(instanceId)
            .withLockDuration(duration)
            .withClock(clock)
            .build()
    }
}

