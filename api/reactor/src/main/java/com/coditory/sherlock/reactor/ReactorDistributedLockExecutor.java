package com.coditory.sherlock.reactor;

import com.coditory.sherlock.reactive.connector.AcquireResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

final class ReactorDistributedLockExecutor {
  private ReactorDistributedLockExecutor() {
    throw new IllegalStateException("Do not instantiate utility class");
  }

  static <T> Mono<T> executeOnAcquired(
      Mono<AcquireResult> lockResult, Supplier<Mono<T>> supplier,
      Supplier<Mono<ReleaseResult>> release) {
    return lockResult
        .filter(AcquireResult::isAcquired)
        .flatMap(acquiredLockResult ->
            supplier.get()
                .flatMap(result -> release.get().map(__ -> result))
                .switchIfEmpty(release.get().then(Mono.empty()))
                .onErrorResume(throwable -> release.get().flatMap(r -> Mono.error(throwable)))
        );
  }

  static <T> Mono<T> executeOnReleased(
      Mono<ReleaseResult> unlockResult, Supplier<Mono<T>> supplier) {
    return unlockResult
        .filter(ReleaseResult::isReleased)
        .flatMap(result -> supplier.get());
  }
}
