package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.DistributedLockBuilder;
import com.coditory.sherlock.LockDuration;
import com.coditory.sherlock.LockId;
import com.coditory.sherlock.OwnerId;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * Use to stub {@link RxSherlock} in tests.
 */
public final class RxSherlockStub implements RxSherlock {
    private final Map<String, RxDistributedLock> locksById = new HashMap<>();
    private boolean defaultLockResult = true;

    /**
     * Make the stub produce released locks by default
     *
     * @return the instance
     */
    @NotNull
    static public RxSherlockStub withReleasedLocks() {
        return new RxSherlockStub()
                .withDefaultAcquireResult(true);
    }

    /**
     * Make the stub produce acquired locks by default
     *
     * @return the instance
     */
    @NotNull
    static public RxSherlockStub withAcquiredLocks() {
        return new RxSherlockStub()
                .withDefaultAcquireResult(false);
    }

    /**
     * Make the stub produce return a predefined lock.
     *
     * @param lock returned when creating a lock with the same id
     * @return the instance
     */
    @NotNull
    public RxSherlockStub withLock(@NotNull RxDistributedLock lock) {
        expectNonNull(lock, "lock");
        this.locksById.put(lock.getId(), lock);
        return this;
    }

    private RxSherlockStub withDefaultAcquireResult(boolean result) {
        this.defaultLockResult = result;
        return this;
    }

    @Override
    @NotNull
    public Single<InitializationResult> initialize() {
        return Single.just(InitializationResult.of(true));
    }

    @Override
    @NotNull
    public DistributedLockBuilder<RxDistributedLock> createLock() {
        return getLockOrDefault();
    }

    @Override
    @NotNull
    public DistributedLockBuilder<RxDistributedLock> createReentrantLock() {
        return getLockOrDefault();
    }

    @Override
    @NotNull
    public DistributedLockBuilder<RxDistributedLock> createOverridingLock() {
        return getLockOrDefault();
    }

    @Override
    @NotNull
    public Single<ReleaseResult> forceReleaseAllLocks() {
        return Single.just(ReleaseResult.of(false));
    }

    private DistributedLockBuilder<RxDistributedLock> getLockOrDefault() {
        return new DistributedLockBuilder<>(this::getLockOrDefault);
    }

    private RxDistributedLock getLockOrDefault(
            LockId id, LockDuration duration, OwnerId ownerId) {
        RxDistributedLockMock defaultLock = RxDistributedLockMock
                .lockStub(id.getValue(), defaultLockResult);
        return locksById.getOrDefault(id.getValue(), defaultLock);
    }
}
