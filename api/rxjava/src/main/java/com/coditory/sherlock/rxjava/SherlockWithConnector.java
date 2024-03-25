package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.DistributedLockBuilder;
import com.coditory.sherlock.DistributedLockBuilder.LockCreator;
import com.coditory.sherlock.OwnerIdPolicy;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import com.coditory.sherlock.rxjava.DelegatingDistributedLock.AcquireAction;
import com.coditory.sherlock.rxjava.DelegatingDistributedLock.ReleaseAction;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class SherlockWithConnector implements Sherlock {
    private final Logger logger = LoggerFactory.getLogger(DistributedLockConnector.class);
    private final DistributedLockConnector connector;
    private final Duration defaultDuration;
    private final OwnerIdPolicy defaultOwnerIdPolicy;

    SherlockWithConnector(
        DistributedLockConnector connector,
        OwnerIdPolicy defaultOwnerIdPolicy,
        Duration defaultDuration
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
    public DistributedLockBuilder<DistributedLock> createLock() {
        return createLockBuilder(connector::acquire, connector::release);
    }

    @Override
    @NotNull
    public DistributedLockBuilder<DistributedLock> createReentrantLock() {
        return createLockBuilder(connector::acquireOrProlong, connector::release);
    }

    @Override
    @NotNull
    public DistributedLockBuilder<DistributedLock> createOverridingLock() {
        return createLockBuilder(connector::forceAcquire, (id, __) -> connector.forceRelease(id));
    }

    @Override
    @NotNull
    public Single<ReleaseResult> forceReleaseAllLocks() {
        return connector.forceReleaseAll();
    }

    private DistributedLockBuilder<DistributedLock> createLockBuilder(
        AcquireAction acquireAction,
        ReleaseAction releaseAction
    ) {
        return new DistributedLockBuilder<>(createLock(acquireAction, releaseAction))
            .withLockDuration(defaultDuration)
            .withOwnerIdPolicy(defaultOwnerIdPolicy);
    }

    private LockCreator<DistributedLock> createLock(
        AcquireAction acquireAction,
        ReleaseAction releaseAction
    ) {
        return (lockId, duration, ownerId) ->
            createLockAndLog(acquireAction, releaseAction, lockId, ownerId, duration);
    }

    private DistributedLock createLockAndLog(
        AcquireAction acquireAction,
        ReleaseAction releaseAction,
        String lockId,
        String ownerId,
        Duration duration
    ) {
        DistributedLock lock = new DelegatingDistributedLock(
            acquireAction, releaseAction, lockId, ownerId, duration);
        logger.debug("Created lock: {}", lock);
        return lock;
    }
}
