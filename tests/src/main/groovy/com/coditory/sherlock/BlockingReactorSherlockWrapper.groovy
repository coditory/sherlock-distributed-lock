package com.coditory.sherlock

import groovy.transform.CompileStatic

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
    DistributedLockBuilder createLock() {
        return blockingLockBuilder(locks.createLock())
    }

    @Override
    DistributedLockBuilder createReentrantLock() {
        return blockingLockBuilder(locks.createReentrantLock())
    }

    @Override
    DistributedLockBuilder createOverridingLock() {
        return blockingLockBuilder(locks.createOverridingLock())
    }

    @Override
    boolean forceReleaseAllLocks() {
        return locks.forceReleaseAllLocks()
            .single().block().released
    }

    @Override
    boolean forceReleaseLock(String lockId) {
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

    BlockingReactorDistributedLock(ReactorDistributedLock lock) {
        this.lock = lock
    }

    @Override
    String getId() {
        return lock.id
    }

    @Override
    boolean acquire() {
        return lock.acquire()
            .single().block().acquired
    }

    @Override
    boolean acquire(Duration duration) {
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
