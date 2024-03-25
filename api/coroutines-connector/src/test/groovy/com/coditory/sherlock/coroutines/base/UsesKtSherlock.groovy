package com.coditory.sherlock.coroutines.base

import com.coditory.sherlock.BlockingKtSherlockWrapper
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.coroutines.Sherlock
import com.coditory.sherlock.inmem.coroutines.InMemorySherlock

import java.time.Clock
import java.time.Duration

trait UsesKtSherlock implements DistributedLocksCreator {
    @Override
    com.coditory.sherlock.Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        Sherlock reactorSherlock = InMemorySherlock.builder()
            .withOwnerId(ownerId)
            .withLockDuration(duration)
            .withClock(clock)
            .build()
        return new BlockingKtSherlockWrapper(reactorSherlock)
    }
}

