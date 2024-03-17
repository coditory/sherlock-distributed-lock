package com.coditory.sherlock.inmem.rxjava

import com.coditory.sherlock.BlockingRxSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.rxjava.RxSherlock

import java.time.Clock
import java.time.Duration

trait UsesReactiveInMemorySherlock implements DistributedLocksCreator {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String collectionName) {
        RxSherlock reactiveLocks = RxInMemorySherlockBuilder.rxInMemorySherlockBuilder()
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return BlockingRxSherlockWrapper.blockingRxSherlock(reactiveLocks)
    }
}
