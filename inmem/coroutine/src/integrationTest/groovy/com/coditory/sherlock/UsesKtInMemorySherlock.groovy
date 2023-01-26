package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingKtSherlockWrapper.blockingKtSherlock
import static com.coditory.sherlock.KtInMemorySherlockBuilder.coroutineInMemorySherlockBuilder

trait UsesKtInMemorySherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
        KtSherlock reactiveLocks = coroutineInMemorySherlockBuilder()
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingKtSherlock(reactiveLocks)
    }
}

