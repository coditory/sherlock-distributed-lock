package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.reactor.ReactorSherlock

import java.time.Clock
import java.time.Duration

import static BlockingReactorSherlockWrapper.blockingReactorSherlock
import static com.coditory.sherlock.inmem.reactor.ReactorInMemorySherlockBuilder.reactorInMemorySherlockBuilder

trait UsesReactorInMemorySherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String collectionName) {
        ReactorSherlock reactorLocks = reactorInMemorySherlockBuilder()
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingReactorSherlock(reactorLocks)
    }
}
