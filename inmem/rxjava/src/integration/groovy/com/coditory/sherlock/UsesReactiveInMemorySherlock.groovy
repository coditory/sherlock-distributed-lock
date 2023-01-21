package com.coditory.sherlock


import com.coditory.sherlock.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

import static BlockingRxSherlockWrapper.blockingRxSherlock
import static RxInMemorySherlockBuilder.rxInMemorySherlockBuilder

trait UsesReactiveInMemorySherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
        RxSherlock reactiveLocks = rxInMemorySherlockBuilder()
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingRxSherlock(reactiveLocks)
    }
}
