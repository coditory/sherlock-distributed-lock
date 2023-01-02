package com.coditory.sherlock;

import com.coditory.sherlock.DistributedLockBuilder.LockCreator;
import com.coditory.sherlock.ReactorDelegatingDistributedLock.AcquireAction;
import com.coditory.sherlock.ReactorDelegatingDistributedLock.ReleaseAction;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class ReactorSherlockWithConnector implements ReactorSherlock {
    private final Logger logger = LoggerFactory.getLogger(ReactorSherlockWithConnector.class);
    private final ReactorDistributedLockConnector connector;
    private final LockDuration defaultDuration;
    private final OwnerIdPolicy defaultOwnerIdPolicy;

    ReactorSherlockWithConnector(
        ReactorDistributedLockConnector connector,
        OwnerIdPolicy defaultOwnerIdPolicy,
        LockDuration defaultDuration
    ) {
        this.connector = expectNonNull(connector, "Expected non null connector");
        this.defaultOwnerIdPolicy =
            expectNonNull(defaultOwnerIdPolicy, "Expected non null defaultOwnerIdPolicy");
        this.defaultDuration =
            expectNonNull(defaultDuration, "Expected non null defaultDuration");
    }

    @Override
    public Mono<InitializationResult> initialize() {
        logger.debug("Initializing sherlock infrastructure");
        return connector.initialize();
    }

    @Override
    public DistributedLockBuilder<ReactorDistributedLock> createLock() {
        return createLockBuilder(connector::acquire, connector::release);
    }

    @Override
    public DistributedLockBuilder<ReactorDistributedLock> createReentrantLock() {
        return createLockBuilder(connector::acquireOrProlong, connector::release);
    }

    @Override
    public DistributedLockBuilder<ReactorDistributedLock> createOverridingLock() {
        return createLockBuilder(connector::forceAcquire, (id, __) -> connector.forceRelease(id));
    }

    @Override
    public Mono<ReleaseResult> forceReleaseAllLocks() {
        return connector.forceReleaseAll();
    }

    private DistributedLockBuilder<ReactorDistributedLock> createLockBuilder(
        AcquireAction acquireAction,
        ReleaseAction releaseAction
    ) {
        return new DistributedLockBuilder<>(createLock(acquireAction, releaseAction))
            .withLockDuration(defaultDuration)
            .withOwnerIdPolicy(defaultOwnerIdPolicy);
    }

    private LockCreator<ReactorDistributedLock> createLock(
        AcquireAction acquireAction,
        ReleaseAction releaseAction
    ) {
        return (lockId, duration, ownerId) ->
            createLockAndLog(acquireAction, releaseAction, lockId, ownerId, duration);
    }

    private ReactorDistributedLock createLockAndLog(
        AcquireAction acquireAction,
        ReleaseAction releaseAction,
        LockId lockId,
        OwnerId ownerId,
        LockDuration duration
    ) {
        ReactorDistributedLock lock = new ReactorDelegatingDistributedLock(
            acquireAction, releaseAction, lockId, ownerId, duration);
        logger.debug("Created lock: {}", lock);
        return lock;
    }
}
