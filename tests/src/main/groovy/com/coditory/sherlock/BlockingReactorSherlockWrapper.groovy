package com.coditory.sherlock

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

import java.time.Duration

// @CompileStatic - groovy compiler throws StackOverflow when uncommented
// it's probably related with implementing an interface with default methods
class BlockingReactorSherlockWrapper implements Sherlock {
    static Sherlock blockingReactorSherlock(ReactorSherlock locks) {
        return new BlockingReactorSherlockWrapper(locks)
    }

    private final ReactorSherlock sherlock

    private BlockingReactorSherlockWrapper(ReactorSherlock sherlock) {
        this.sherlock = sherlock
    }

    ReactorSherlock unwrap() {
        return sherlock
    }

    @Override
    void initialize() {
        sherlock.initialize()
                .single().block()
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
    boolean forceReleaseAllLocks() {
        return sherlock.forceReleaseAllLocks()
                .single().block().released
    }

    @Override
    boolean forceReleaseLock(@NotNull String lockId) {
        return createOverridingLock(lockId)
                .release()
    }

    private DistributedLockBuilder blockingLockBuilder(
            DistributedLockBuilder<ReactorDistributedLock> reactiveBuilder) {
        return reactiveBuilder.withMappedLock({ lock -> new BlockingReactorDistributedLock(lock) })
    }
}

@CompileStatic
class BlockingReactorDistributedLock implements DistributedLock {
    private final ReactorDistributedLock lock

    BlockingReactorDistributedLock(@NotNull ReactorDistributedLock lock) {
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
                .single().block().acquired
    }

    @Override
    boolean acquire(@NotNull Duration duration) {
        return lock.acquire(duration)
                .single().block().acquired
    }

    @Override
    boolean acquireForever() {
        return lock.acquireForever()
                .single().block().acquired
    }

    @Override
    boolean release() {
        return lock.release()
                .single().block().released
    }
}
