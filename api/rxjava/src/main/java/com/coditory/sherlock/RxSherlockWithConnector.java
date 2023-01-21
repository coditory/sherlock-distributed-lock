package com.coditory.sherlock;

import com.coditory.sherlock.DistributedLockBuilder.LockCreator;
import com.coditory.sherlock.RxDelegatingDistributedLock.AcquireAction;
import com.coditory.sherlock.RxDelegatingDistributedLock.ReleaseAction;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class RxSherlockWithConnector implements RxSherlock {
    private final Logger logger = LoggerFactory.getLogger(RxDistributedLockConnector.class);
    private final RxDistributedLockConnector connector;
    private final LockDuration defaultDuration;
    private final OwnerIdPolicy defaultOwnerIdPolicy;

    RxSherlockWithConnector(
            RxDistributedLockConnector connector,
            OwnerIdPolicy defaultOwnerIdPolicy,
            LockDuration defaultDuration
    ) {
        expectNonNull(connector, "connector");
        expectNonNull(defaultOwnerIdPolicy, "defaultOwnerIdPolicy");
        expectNonNull(defaultDuration, "defaultDuration");
        this.connector = connector;
        this.defaultOwnerIdPolicy = defaultOwnerIdPolicy;
        this.defaultDuration = defaultDuration;
    }

    @Override
    @NotNull
    public Single<InitializationResult> initialize() {
        logger.debug("Initializing sherlock infrastructure");
        return connector.initialize();
    }

    @Override
    @NotNull
    public DistributedLockBuilder<RxDistributedLock> createLock() {
        return createLockBuilder(connector::acquire, connector::release);
    }

    @Override
    @NotNull
    public DistributedLockBuilder<RxDistributedLock> createReentrantLock() {
        return createLockBuilder(connector::acquireOrProlong, connector::release);
    }

    @Override
    @NotNull
    public DistributedLockBuilder<RxDistributedLock> createOverridingLock() {
        return createLockBuilder(connector::forceAcquire, (id, __) -> connector.forceRelease(id));
    }

    @Override
    @NotNull
    public Single<ReleaseResult> forceReleaseAllLocks() {
        return connector.forceReleaseAll();
    }

    private DistributedLockBuilder<RxDistributedLock> createLockBuilder(
            AcquireAction acquireAction,
            ReleaseAction releaseAction
    ) {
        return new DistributedLockBuilder<>(createLock(acquireAction, releaseAction))
                .withLockDuration(defaultDuration)
                .withOwnerIdPolicy(defaultOwnerIdPolicy);
    }

    private LockCreator<RxDistributedLock> createLock(
            AcquireAction acquireAction,
            ReleaseAction releaseAction
    ) {
        return (lockId, duration, ownerId) ->
                createLockAndLog(acquireAction, releaseAction, lockId, ownerId, duration);
    }

    private RxDistributedLock createLockAndLog(
            AcquireAction acquireAction,
            ReleaseAction releaseAction,
            LockId lockId,
            OwnerId ownerId,
            LockDuration duration
    ) {
        RxDistributedLock lock = new RxDelegatingDistributedLock(
                acquireAction, releaseAction, lockId, ownerId, duration);
        logger.debug("Created lock: {}", lock);
        return lock;
    }
}
