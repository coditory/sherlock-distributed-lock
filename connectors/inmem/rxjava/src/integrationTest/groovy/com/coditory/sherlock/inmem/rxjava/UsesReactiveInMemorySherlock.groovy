package com.coditory.sherlock.inmem.rxjava

import com.coditory.sherlock.BlockingRxSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

trait UsesReactiveInMemorySherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String collectionName) {
        com.coditory.sherlock.rxjava.Sherlock reactiveLocks = InMemorySherlock.builder()
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return new BlockingRxSherlockWrapper(reactiveLocks)
    }
}
