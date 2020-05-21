package com.coditory.sherlock;

import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Use to stub {@link ReactorSherlock} in tests.
 */
public final class ReactorSherlockStub implements ReactorSherlock {
    private final Map<String, ReactorDistributedLock> locksById = new HashMap<>();
    private boolean defaultLockResult = true;

    /**
     * Make the stub produce released locks by default
     *
     * @return the instance
     */
    static public ReactorSherlockStub withReleasedLocks() {
        return new ReactorSherlockStub()
                .withDefaultAcquireResult(true);
    }

    /**
     * Make the stub produce acquired locks by default
     *
     * @return the instance
     */
    static public ReactorSherlockStub withAcquiredLocks() {
        return new ReactorSherlockStub()
                .withDefaultAcquireResult(false);
    }

    /**
     * Make the stub produce return a predefined lock.
     *
     * @param lock returned when creating a lock with the same id
     * @return the instance
     */
    public ReactorSherlockStub withLock(ReactorDistributedLock lock) {
        this.locksById.put(lock.getId(), lock);
        return this;
    }

    private ReactorSherlockStub withDefaultAcquireResult(boolean result) {
        this.defaultLockResult = result;
        return this;
    }

    @Override
    public Mono<InitializationResult> initialize() {
        return Mono.just(InitializationResult.of(true));
    }

    @Override
    public DistributedLockBuilder<ReactorDistributedLock> createLock() {
        return getLockOrDefault();
    }

    @Override
    public DistributedLockBuilder<ReactorDistributedLock> createReentrantLock() {
        return getLockOrDefault();
    }

    @Override
    public DistributedLockBuilder<ReactorDistributedLock> createOverridingLock() {
        return getLockOrDefault();
    }

    @Override
    public Mono<ReleaseResult> forceReleaseAllLocks() {
        return Mono.just(ReleaseResult.FAILURE);
    }

    private DistributedLockBuilder<ReactorDistributedLock> getLockOrDefault() {
        return new DistributedLockBuilder<>(this::getLockOrDefault);
    }

    private ReactorDistributedLock getLockOrDefault(
            LockId id, LockDuration duration, OwnerId ownerId) {
        ReactorDistributedLockMock defaultLock = ReactorDistributedLockMock
                .lockStub(id.getValue(), defaultLockResult);
        return locksById.getOrDefault(id.getValue(), defaultLock);
    }
}
