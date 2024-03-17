package com.coditory.sherlock.rxjava.base

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.rxjava.RxSherlock

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingRxSherlockWrapper.blockingRxSherlock
import static com.coditory.sherlock.inmem.rxjava.RxInMemorySherlockBuilder.rxInMemorySherlockBuilder

trait UsesReactorSherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        RxSherlock rxSherlock = rxInMemorySherlockBuilder()
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingRxSherlock(rxSherlock)
    }
}
