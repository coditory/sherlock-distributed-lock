package com.coditory.sherlock.inmem.coroutines

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.coroutines.KtSherlock

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingKtSherlockWrapper.blockingKtSherlock
import static com.coditory.sherlock.inmem.coroutines.KtInMemorySherlockBuilder.coroutineInMemorySherlockBuilder

trait UsesKtInMemorySherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String collectionName) {
        KtSherlock reactiveLocks = coroutineInMemorySherlockBuilder()
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingKtSherlock(reactiveLocks)
    }
}

