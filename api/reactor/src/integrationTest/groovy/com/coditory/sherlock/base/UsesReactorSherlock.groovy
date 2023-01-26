package com.coditory.sherlock.base

import com.coditory.sherlock.ReactorSherlock
import com.coditory.sherlock.Sherlock

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingReactorSherlockWrapper.blockingReactorSherlock
import static com.coditory.sherlock.ReactorInMemorySherlockBuilder.reactorInMemorySherlockBuilder

trait UsesReactorSherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock) {
        ReactorSherlock reactorSherlock = reactorInMemorySherlockBuilder()
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingReactorSherlock(reactorSherlock)
    }
}
