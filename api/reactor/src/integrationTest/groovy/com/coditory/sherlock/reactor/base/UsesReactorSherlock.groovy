package com.coditory.sherlock.reactor.base

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.reactor.ReactorSherlock

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingReactorSherlockWrapper.blockingReactorSherlock
import static com.coditory.sherlock.inmem.reactor.ReactorInMemorySherlockBuilder.reactorInMemorySherlockBuilder

trait UsesReactorSherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        ReactorSherlock reactorSherlock = reactorInMemorySherlockBuilder()
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingReactorSherlock(reactorSherlock)
    }
}
