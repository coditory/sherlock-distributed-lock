package com.coditory.sherlock;

import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Single;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class RxSherlockWrapper implements RxSherlock {
    private final ReactiveSherlock sherlock;

    RxSherlockWrapper(ReactiveSherlock sherlock) {
        this.sherlock = expectNonNull(sherlock, "Expected non null sherlock");
    }

    @Override
    public Single<InitializationResult> initialize() {
        return PublisherToSingleConverter.convertToSingle(sherlock.initialize());
    }

    @Override
    public DistributedLockBuilder<RxDistributedLock> createLock() {
        return createLockBuilder(sherlock.createLock());
    }

    @Override
    public DistributedLockBuilder<RxDistributedLock> createReentrantLock() {
        return createLockBuilder(sherlock.createReentrantLock());
    }

    @Override
    public DistributedLockBuilder<RxDistributedLock> createOverridingLock() {
        return createLockBuilder(sherlock.createOverridingLock());
    }

    @Override
    public Single<ReleaseResult> forceReleaseAllLocks() {
        return PublisherToSingleConverter.convertToSingle(sherlock.forceReleaseAllLocks());
    }

    private DistributedLockBuilder<RxDistributedLock> createLockBuilder(
            DistributedLockBuilder<ReactiveDistributedLock> reactiveBuilder) {
        return reactiveBuilder.withMappedLock(RxDistributedLockWrapper::new);
    }
}
