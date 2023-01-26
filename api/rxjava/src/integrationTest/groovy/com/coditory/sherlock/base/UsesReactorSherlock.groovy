package com.coditory.sherlock.base

import com.coditory.sherlock.RxSherlock
import com.coditory.sherlock.Sherlock

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingRxSherlockWrapper.blockingRxSherlock
import static com.coditory.sherlock.RxInMemorySherlockBuilder.rxInMemorySherlockBuilder

trait UsesReactorSherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock) {
        RxSherlock rxSherlock = rxInMemorySherlockBuilder()
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingRxSherlock(rxSherlock)
    }
}
