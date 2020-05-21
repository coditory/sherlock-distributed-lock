package com.coditory.sherlock;

import com.coditory.sherlock.DelegatingDistributedLock.AcquireAction;
import com.coditory.sherlock.DelegatingDistributedLock.ReleaseAction;
import com.coditory.sherlock.DistributedLockBuilder.LockCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class SherlockWithConnector implements Sherlock {
    private final Logger logger = LoggerFactory.getLogger(SherlockWithConnector.class);
    private final DistributedLockConnector connector;
    private final LockDuration defaultDuration;
    private final OwnerIdPolicy defaultOwnerIdPolicy;

    SherlockWithConnector(
            DistributedLockConnector connector,
            OwnerIdPolicy defaultOwnerIdPolicy,
            LockDuration defaultDuration) {
        this.connector = expectNonNull(connector, "Expected non null connector");
        this.defaultOwnerIdPolicy =
                expectNonNull(defaultOwnerIdPolicy, "Expected non null defaultOwnerIdPolicy");
        this.defaultDuration =
                expectNonNull(defaultDuration, "Expected non null defaultDuration");
    }

    @Override
    public void initialize() {
        logger.debug("Initializing sherlock infrastructure");
        connector.initialize();
    }

    @Override
    public DistributedLockBuilder<DistributedLock> createLock() {
        return createLockBuilder(connector::acquire, connector::release);
    }

    @Override
    public DistributedLockBuilder<DistributedLock> createReentrantLock() {
        return createLockBuilder(connector::acquireOrProlong, connector::release);
    }

    @Override
    public DistributedLockBuilder<DistributedLock> createOverridingLock() {
        return createLockBuilder(connector::forceAcquire, (id, __) -> connector.forceRelease(id));
    }

    @Override
    public boolean forceReleaseAllLocks() {
        return connector.forceReleaseAll();
    }

    private DistributedLockBuilder<DistributedLock> createLockBuilder(
            AcquireAction acquireAction,
            ReleaseAction releaseAction) {
        return new DistributedLockBuilder<>(createLock(acquireAction, releaseAction))
                .withLockDuration(defaultDuration)
                .withOwnerIdPolicy(defaultOwnerIdPolicy);
    }

    private LockCreator<DistributedLock> createLock(
            AcquireAction acquireAction,
            ReleaseAction releaseAction) {
        return (lockId, duration, ownerId) ->
                createLockAndLog(acquireAction, releaseAction, lockId, ownerId, duration);
    }

    private DistributedLock createLockAndLog(
            AcquireAction acquireAction,
            ReleaseAction releaseAction,
            LockId lockId,
            OwnerId ownerId,
            LockDuration duration) {
        DistributedLock lock = new DelegatingDistributedLock(
                acquireAction, releaseAction, lockId, ownerId, duration);
        logger.debug("Created lock: {}", lock);
        return lock;
    }
}
