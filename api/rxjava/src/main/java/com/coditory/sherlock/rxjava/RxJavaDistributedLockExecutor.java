package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.reactive.connector.LockResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.function.Supplier;

final class RxJavaDistributedLockExecutor {
  private RxJavaDistributedLockExecutor() {
    throw new IllegalStateException("Do not instantiate utility class");
  }

  static <T> Maybe<T> executeOnAcquired(
      Single<LockResult> lockResult, Supplier<Single<T>> supplier,
      Supplier<Single<ReleaseResult>> release) {
    return lockResult
        .filter(LockResult::isLocked)
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
        .filter(ReleaseResult::isUnlocked)
        .flatMap(releasedLockResult -> supplier.get().flatMapMaybe(Maybe::just));
  }
}
