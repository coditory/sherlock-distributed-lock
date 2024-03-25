package com.coditory.sherlock.test;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.DistributedLockBuilder;
import com.coditory.sherlock.Sherlock;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * Used to stub {@link Sherlock} in tests.
 */
public final class SherlockStub implements Sherlock {
    private final Map<String, DistributedLock> locksById = new HashMap<>();
    private boolean defaultLockResult = true;

    /**
     * Make the stub produce released locks by default
     *
     * @return the stub instance
     */
    @NotNull
    public static SherlockStub withReleasedLocks() {
        return new SherlockStub()
            .withDefaultAcquireResult(true);
    }

    /**
     * Make the stub produce acquired locks by default
     *
     * @return the stub instance
     */
    @NotNull
    public static SherlockStub withAcquiredLocks() {
        return new SherlockStub()
            .withDefaultAcquireResult(false);
    }

    /**
     * Make the stub produce return a predefined lock.
     *
     * @param lock returned when creating a lock with the same id
     * @return the stub instance
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
    public void initialize() {
        // deliberately empty, nothing to initialize
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
    public boolean forceReleaseAllLocks() {
        // deliberately empty
        return false;
    }

    private DistributedLockBuilder<DistributedLock> getLockOrDefault() {
        return new DistributedLockBuilder<>(this::getLockOrDefault);
    }

    private DistributedLock getLockOrDefault(String id, Duration duration, String ownerId) {
        DistributedLockMock defaultLock = DistributedLockMock.lockStub(id, defaultLockResult);
        return locksById.getOrDefault(id, defaultLock);
    }
}
