package com.coditory.sherlock.inmem.reactor

import com.coditory.sherlock.BlockingReactorSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

trait UsesReactorInMemorySherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String collectionName) {
        com.coditory.sherlock.reactor.Sherlock reactorLocks = InMemorySherlock.builder()
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return new BlockingReactorSherlockWrapper(reactorLocks)
    }
}
