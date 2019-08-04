package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.function.Supplier;

final class RxDistributedLockExecutor {
  private RxDistributedLockExecutor() {
    throw new IllegalStateException("Do not instantiate utility class");
  }

  static <T> Maybe<T> executeOnAcquired(
      Single<AcquireResult> lockResult, Supplier<Single<T>> supplier,
      Supplier<Single<ReleaseResult>> release) {
    return lockResult
        .filter(AcquireResult::isAcquired)
        .flatMap(acquiredLockResult ->
          Maybe.fromSingle(supplier.get())
              .flatMap(result -> release.get().flatMapMaybe(__ -> Maybe.just(result)))
              .switchIfEmpty(release.get().flatMapMaybe(__ -> Maybe.empty()))
              .onErrorResumeNext((Throwable throwable) -> release.get().flatMapMaybe(r -> Maybe.error(throwable)))
        );
  }

  static <T> Maybe<T> executeOnReleased(
      Single<ReleaseResult> unlockResult, Supplier<Single<T>> supplier) {
    return unlockResult
        .filter(ReleaseResult::isReleased)
        .flatMap(releasedLockResult -> supplier.get().flatMapMaybe(Maybe::just));
  }
}
