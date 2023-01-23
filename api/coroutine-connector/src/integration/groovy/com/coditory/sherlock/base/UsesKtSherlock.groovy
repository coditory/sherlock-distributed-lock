package com.coditory.sherlock.base


import com.coditory.sherlock.KtSherlock
import com.coditory.sherlock.Sherlock

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingKtSherlockWrapper.blockingKtSherlock
import static com.coditory.sherlock.KtInMemorySherlockBuilder.coroutineInMemorySherlockBuilder

trait UsesKtSherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock) {
        KtSherlock reactorSherlock = coroutineInMemorySherlockBuilder()
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingKtSherlock(reactorSherlock)
    }
}
