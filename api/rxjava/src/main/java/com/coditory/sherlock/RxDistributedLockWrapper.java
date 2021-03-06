package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Single;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

final class RxDistributedLockWrapper implements RxDistributedLock {
    private final LockResultLogger logger;
    private final ReactiveDistributedLock lock;

    RxDistributedLockWrapper(ReactiveDistributedLock lock) {
        this.lock = lock;
        this.logger = new LockResultLogger(lock.getId(), lock.getClass());
    }

    @Override
    public String getId() {
        return lock.getId();
    }

    @Override
    public Single<AcquireResult> acquire() {
        return toSingle(lock.acquire())
                .doOnSuccess(logger::logResult);
    }

    @Override
    public Single<AcquireResult> acquire(Duration duration) {
        return toSingle(lock.acquire(duration))
                .doOnSuccess(logger::logResult);
    }

    @Override
    public Single<AcquireResult> acquireForever() {
        return toSingle(lock.acquireForever())
                .doOnSuccess(logger::logResult);
    }

    @Override
    public Single<ReleaseResult> release() {
        return toSingle(lock.release())
                .doOnSuccess(logger::logResult);
    }

    private <T> Single<T> toSingle(Publisher<T> publisher) {
        return PublisherToSingleConverter.convertToSingle(publisher);
    }
}
