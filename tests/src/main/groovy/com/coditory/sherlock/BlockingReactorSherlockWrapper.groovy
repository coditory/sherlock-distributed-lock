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

    private final ReactorSherlock locks

    private BlockingReactorSherlockWrapper(ReactorSherlock locks) {
        this.locks = locks
    }

    @Override
    void initialize() {
        locks.initialize()
                .single().block()
    }

    @Override
    @NotNull
    DistributedLockBuilder createLock() {
        return blockingLockBuilder(locks.createLock())
    }

    @Override
    @NotNull
    DistributedLockBuilder createReentrantLock() {
        return blockingLockBuilder(locks.createReentrantLock())
    }

    @Override
    @NotNull
    DistributedLockBuilder createOverridingLock() {
        return blockingLockBuilder(locks.createOverridingLock())
    }

    @Override
    boolean forceReleaseAllLocks() {
        return locks.forceReleaseAllLocks()
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
