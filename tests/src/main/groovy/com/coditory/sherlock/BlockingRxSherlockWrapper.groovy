package com.coditory.sherlock

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

import java.time.Duration

import static java.util.Objects.requireNonNull

// @CompileStatic - groovy compiler throws StackOverflow when uncommented
// it's probably related with implementing an interface with default methods
class BlockingRxSherlockWrapper implements Sherlock {
    private final com.coditory.sherlock.rxjava.Sherlock sherlock

    BlockingRxSherlockWrapper(com.coditory.sherlock.rxjava.Sherlock sherlock) {
        requireNonNull(sherlock)
        this.sherlock = sherlock
    }

    com.coditory.sherlock.rxjava.Sherlock unwrap() {
        return sherlock
    }

    @Override
    void initialize() {
        sherlock.initialize()
                .blockingGet()
    }

    @Override
    @NotNull
    DistributedLockBuilder createLock() {
        return blockingLockBuilder(sherlock.createLock())
    }

    @Override
    @NotNull
    DistributedLockBuilder createReentrantLock() {
        return blockingLockBuilder(sherlock.createReentrantLock())
    }

    @Override
    @NotNull
    DistributedLockBuilder createOverridingLock() {
        return blockingLockBuilder(sherlock.createOverridingLock())
    }

    @Override
    @NotNull
    boolean forceReleaseAllLocks() {
        return sherlock.forceReleaseAllLocks()
                .blockingGet().released
    }

    @Override
    boolean forceReleaseLock(@NotNull String lockId) {
        return createOverridingLock(lockId)
                .release()
    }

    private DistributedLockBuilder blockingLockBuilder(DistributedLockBuilder<com.coditory.sherlock.rxjava.DistributedLock> reactiveBuilder) {
        return reactiveBuilder.withMappedLock({ lock -> new BlockingRxDistributedLock(lock) })
    }
}


@CompileStatic
class BlockingRxDistributedLock implements DistributedLock {
    private final com.coditory.sherlock.rxjava.DistributedLock lock

    BlockingRxDistributedLock(com.coditory.sherlock.rxjava.DistributedLock lock) {
        requireNonNull(lock)
        this.lock = lock
    }

    @Override
    @NotNull
    String getId() {
        return lock.id
    }

    @Override
    boolean acquire() {
        return lock.acquire()
                .blockingGet().acquired
    }

    @Override
    boolean acquire(@NotNull Duration duration) {
        return lock.acquire(duration)
                .blockingGet().acquired
    }

    @Override
    boolean acquireForever() {
        return lock.acquireForever()
                .blockingGet().acquired
    }

    @Override
    boolean release() {
        return lock.release()
                .blockingGet().released
    }
}
