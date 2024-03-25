package com.coditory.sherlock.inmem.coroutines

import com.coditory.sherlock.BlockingKtSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

trait UsesKtInMemorySherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String collectionName) {
        com.coditory.sherlock.coroutines.Sherlock reactiveLocks = InMemorySherlock.builder()
            .withOwnerId(instanceId)
            .withLockDuration(duration)
            .withClock(clock)
            .build()
        return new BlockingKtSherlockWrapper(reactiveLocks)
    }
}


