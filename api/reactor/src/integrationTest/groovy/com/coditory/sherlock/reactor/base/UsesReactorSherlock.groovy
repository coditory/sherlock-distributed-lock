package com.coditory.sherlock.reactor.base

import com.coditory.sherlock.BlockingReactorSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.inmem.reactor.InMemorySherlock

import java.time.Clock
import java.time.Duration

trait UsesReactorSherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        com.coditory.sherlock.reactor.Sherlock reactorSherlock = InMemorySherlock.builder()
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return new BlockingReactorSherlockWrapper(reactorSherlock)
    }
}
