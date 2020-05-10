package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

final class ReactorDistributedLockExecutor {
  private ReactorDistributedLockExecutor() {
    throw new IllegalStateException("Do not instantiate utility class");
  }

  static <T> Mono<T> executeOnAcquired(
      Mono<AcquireResult> lockResult,
      Mono<T> onAcquired,
      Supplier<Mono<ReleaseResult>> release
  ) {
    Mono<T> onAcquiredWithRelease = onAcquired
        .flatMap(result -> release.get().map(__ -> result))
        .switchIfEmpty(release.get().then(Mono.empty()))
        .onErrorResume(throwable -> release.get().flatMap(r -> Mono.error(throwable)));
    return lockResult
        .filter(AcquireResult::isAcquired)
        .flatMap(__ -> onAcquiredWithRelease);
  }

  static <T> Mono<T> executeOnAcquired(
      Mono<AcquireResult> lockResult,
      Mono<T> mono, Mono<T> onNotAcquired,
      Supplier<Mono<ReleaseResult>> release
  ) {
    Mono<T> onAcquiredWithRelease = mono
        .flatMap(result -> release.get().map(__ -> result))
        .switchIfEmpty(release.get().then(Mono.empty()))
        .onErrorResume(throwable -> release.get().flatMap(r -> Mono.error(throwable)));
    return lockResult
        .filter(AcquireResult::isAcquired)
        .flatMap(acquiredLockResult ->
            acquiredLockResult.isAcquired()
                ? onAcquiredWithRelease
                : onNotAcquired
        );
  }

  static <T> Flux<T> executeOnAcquired(
      Mono<AcquireResult> lockResult,
      Flux<T> flux,
      Supplier<Mono<ReleaseResult>> release
  ) {
    return executeOnAcquired(lockResult, flux, Flux.empty(), release);
  }

  static <T> Flux<T> executeOnAcquired(
      Mono<AcquireResult> lockResult,
      Flux<T> flux,
      Flux<T> onNotAcquired,
      Supplier<Mono<ReleaseResult>> release
  ) {
    Flux<T> onAcquiredWithRelease = flux
        .concatWith(release.get().then(Mono.empty()))
        .onErrorResume(throwable -> release.get().flatMap(r -> Mono.error(throwable)));
    return lockResult
        .flatMapMany(acquireResult ->
            acquireResult.isAcquired()
                ? onAcquiredWithRelease
                : onNotAcquired
        );
  }

  static <T> Mono<T> executeOnReleased(
      Mono<ReleaseResult> unlockResult, Mono<T> onReleased) {
    return unlockResult
        .filter(ReleaseResult::isReleased)
        .flatMap(result -> onReleased);
  }

  static <T> Flux<T> executeOnReleased(
      Mono<ReleaseResult> unlockResult, Flux<T> onReleased) {
    return unlockResult
        .filter(ReleaseResult::isReleased)
        .flatMapMany(result -> onReleased);
  }
}
