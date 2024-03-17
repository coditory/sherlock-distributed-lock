package com.coditory.sherlock.reactor;

import com.coditory.sherlock.DistributedLockBuilder;
import com.coditory.sherlock.LockDuration;
import com.coditory.sherlock.LockId;
import com.coditory.sherlock.OwnerId;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * Use to stub {@link Sherlock} in tests.
 */
public final class SherlockStub implements Sherlock {
    private final Map<String, DistributedLock> locksById = new HashMap<>();
    private boolean defaultLockResult = true;

    /**
     * Make the stub produce released locks by default
     *
     * @return the instance
     */
    @NotNull
    static public SherlockStub withReleasedLocks() {
        return new SherlockStub()
                .withDefaultAcquireResult(true);
    }

    /**
     * Make the stub produce acquired locks by default
     *
     * @return the instance
     */
    @NotNull
    static public SherlockStub withAcquiredLocks() {
        return new SherlockStub()
                .withDefaultAcquireResult(false);
    }

    /**
     * Make the stub produce return a predefined lock.
     *
     * @param lock returned when creating a lock with the same id
     * @return the instance
     */
    @NotNull
    public SherlockStub withLock(@NotNull DistributedLock lock) {
        expectNonNull(lock, "lock");
        this.locksById.put(lock.getId(), lock);
        return this;
    }

    private SherlockStub withDefaultAcquireResult(boolean result) {
        this.defaultLockResult = result;
        return this;
    }

    @Override
    @NotNull
    public Mono<InitializationResult> initialize() {
        return Mono.just(InitializationResult.of(true));
    }

    @Override
    @NotNull
    public DistributedLockBuilder<DistributedLock> createLock() {
        return getLockOrDefault();
    }

    @Override
    @NotNull
    public DistributedLockBuilder<DistributedLock> createReentrantLock() {
        return getLockOrDefault();
    }

    @Override
    @NotNull
    public DistributedLockBuilder<DistributedLock> createOverridingLock() {
        return getLockOrDefault();
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> forceReleaseAllLocks() {
        return Mono.just(ReleaseResult.FAILURE);
    }

    private DistributedLockBuilder<DistributedLock> getLockOrDefault() {
        return new DistributedLockBuilder<>(this::getLockOrDefault);
    }

    private DistributedLock getLockOrDefault(
            LockId id, LockDuration duration, OwnerId ownerId) {
        DistributedLockMock defaultLock = DistributedLockMock
                .lockStub(id.getValue(), defaultLockResult);
        return locksById.getOrDefault(id.getValue(), defaultLock);
    }
}
