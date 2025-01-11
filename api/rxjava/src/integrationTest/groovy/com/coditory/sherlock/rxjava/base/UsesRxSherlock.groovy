package com.coditory.sherlock.rxjava.base

import com.coditory.sherlock.BlockingRxSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.inmem.rxjava.InMemorySherlock

import java.time.Clock
import java.time.Duration

trait UsesRxSherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        com.coditory.sherlock.rxjava.Sherlock rxSherlock = InMemorySherlock.builder()
            .withOwnerId(ownerId)
            .withLockDuration(duration)
            .withClock(clock)
            .build()
        return new BlockingRxSherlockWrapper(rxSherlock)
    }
}
