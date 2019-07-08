package com.coditory.sherlock.reactor;

import com.coditory.sherlock.reactive.driver.LockResult;
import com.coditory.sherlock.reactive.driver.UnlockResult;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Supplier;

import static com.coditory.sherlock.reactor.ReactorDistributedLockExecutor.executeOnAcquired;
import static com.coditory.sherlock.reactor.ReactorDistributedLockExecutor.executeOnReleased;

public interface ReactorDistributedLock {
  String getId();

  Mono<LockResult> acquire();

  Mono<LockResult> acquire(Duration duration);

  Mono<LockResult> acquireForever();

  Mono<UnlockResult> release();

  default <T> Mono<T> acquireAndExecute(Supplier<Mono<T>> action) {
    return executeOnAcquired(acquire(), action, this::release);
  }

  default <T> Mono<T> acquireAndExecute(Duration duration, Supplier<Mono<T>> action) {
    return executeOnAcquired(acquire(duration), action, this::release);
  }

  default <T> Mono<T> acquireForeverAndExecute(Supplier<Mono<T>> action) {
    return executeOnAcquired(acquireForever(), action, this::release);
  }

  default <T> Mono<T> releaseAndExecute(Supplier<Mono<T>> action) {
    return executeOnReleased(release(), action);
  }
}
