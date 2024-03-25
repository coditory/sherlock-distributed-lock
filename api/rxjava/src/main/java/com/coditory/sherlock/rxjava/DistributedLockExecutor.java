package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.function.Supplier;

final class DistributedLockExecutor {
    private DistributedLockExecutor() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    static <T> Maybe<T> executeOnAcquired(
        Single<AcquireResult> lockResult,
        Single<T> single,
        Supplier<Single<ReleaseResult>> release
    ) {
        return lockResult
            .filter(AcquireResult::isAcquired)
            .flatMap(acquiredLockResult ->
                Maybe.fromSingle(single)
                    .flatMap(result -> release.get().flatMapMaybe(__ -> Maybe.just(result)))
                    .switchIfEmpty(release.get().flatMapMaybe(__ -> Maybe.empty()))
                    .onErrorResumeNext((Throwable throwable) -> release.get().flatMapMaybe(r -> Maybe.error(throwable)))
            );
    }
}
