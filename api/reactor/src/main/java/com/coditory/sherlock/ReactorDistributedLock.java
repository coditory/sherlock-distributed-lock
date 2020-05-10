package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

import static com.coditory.sherlock.LockState.ACQUIRED;
import static com.coditory.sherlock.LockState.LOCKED;
import static com.coditory.sherlock.LockState.UNLOCKED;
import static com.coditory.sherlock.ReactorDistributedLockExecutor.executeOnAcquired;
import static com.coditory.sherlock.ReactorDistributedLockExecutor.executeOnReleased;

/**
 * A reactive distributed lock with Reactor API.
 *
 * @see ReactiveSherlock
 */
public interface ReactorDistributedLock {
  /**
   * Return the lock id.
   *
   * @return the lock id
   */
  String getId();

  /**
   * Try to acquire the lock. Lock is acquired for a pre configured duration.
   *
   * @return {@link AcquireResult#SUCCESS} if lock is acquired
   */
  Mono<AcquireResult> acquire();

  /**
   * Try to acquire the lock for a given duration.
   *
   * @param duration how much time must pass for the acquired lock to expire
   * @return {@link AcquireResult#SUCCESS} if lock is acquired
   */
  Mono<AcquireResult> acquire(Duration duration);

  /**
   * Try to acquire the lock without expiring date.
   * <p>
   * It is potentially dangerous. Lookout for a situation when the lock owning instance goes down
   * with out releasing the lock.
   *
   * @return {@link AcquireResult#SUCCESS} if lock is acquired
   */
  Mono<AcquireResult> acquireForever();

  /**
   * Try to release the lock.
   *
   * @return {@link ReleaseResult#SUCCESS} if lock was released by this method invocation. If lock
   * has expired or was released earlier  then {@link ReleaseResult#FAILURE} is returned.
   */
  Mono<ReleaseResult> release();

  /**
   * Get current lock state.
   *
   * @return current lock state
   */
  Mono<LockState> getState();

  /**
   * Check if lock is acquired by this instance.
   *
   * @return true if lock is acquired by this instance
   */
  default Mono<Boolean> isAcquired() {
    return getState().map(ACQUIRED::equals);
  }

  /**
   * Check if lock is locked by any instance.
   *
   * @return true if lock is acquired by any instance
   */
  default Mono<Boolean> isLocked() {
    return getState().map(LOCKED::equals);
  }

  /**
   * Check if lock is released.
   *
   * @return true if lock is released
   */
  default Mono<Boolean> isReleased() {
    return getState().map(UNLOCKED::equals);
  }

  /**
   * Acquire a lock and release it after action is executed or fails.
   *
   * @param <T>        type od value emitted by the action
   * @param onAcquired mono that is executed when lock is acquired
   * @return items from onAcquired if lock was acquired, otherwise Mono.empty()
   * @see ReactorDistributedLock#acquire()
   */
  default <T> Mono<T> acquireAndExecute(Mono<T> onAcquired) {
    return executeOnAcquired(acquire(), onAcquired, this::release);
  }

  /**
   * Acquire a lock and release it after action is executed or fails.
   *
   * @param <T>           type od value emitted by the action
   * @param onAcquired    executed when lock is acquired
   * @param onNotAcquired executed when lock is not acquired
   * @return items from onAcquired if lock was acquired, otherwise Mono.empty()
   * @see ReactorDistributedLock#acquire()
   */
  default <T> Mono<T> acquireAndExecute(Mono<T> onAcquired, Mono<T> onNotAcquired) {
    return executeOnAcquired(acquire(), onAcquired, onNotAcquired, this::release);
  }

  /**
   * Acquire a lock and release it after action is executed or fails.
   *
   * @param <T>        type od value emitted by the action
   * @param onAcquired flux that is executed when lock is acquired
   * @return items from onAcquired if lock was acquired, otherwise Flux.empty()
   * @see ReactorDistributedLock#acquire()
   */
  default <T> Flux<T> acquireAndExecute(Flux<T> onAcquired) {
    return executeOnAcquired(acquire(), onAcquired, this::release);
  }

  /**
   * Acquire a lock and release it after action is executed or fails.
   *
   * @param <T>           type od value emitted by the action
   * @param onAcquired    executed when lock is acquired
   * @param onNotAcquired executed when lock is not acquired
   * @return items from onAcquired if lock was acquired, otherwise items from onNotAcquired
   * @see ReactorDistributedLock#acquire()
   */
  default <T> Flux<T> acquireAndExecute(Flux<T> onAcquired, Flux<T> onNotAcquired) {
    return executeOnAcquired(acquire(), onAcquired, onNotAcquired, this::release);
  }

  /**
   * Acquire a lock for a given duration and release it after action is executed.
   *
   * @param <T>        type od value emitted by the action
   * @param duration   how much time must pass for the acquired lock to expire
   * @param onAcquired executed when lock is acquired
   * @return items from onAcquired if lock was acquired, otherwise Mono.empty()
   * @see ReactorDistributedLock#acquire(Duration)
   */
  default <T> Mono<T> acquireAndExecute(Duration duration, Mono<T> onAcquired) {
    return executeOnAcquired(acquire(duration), onAcquired, this::release);
  }

  /**
   * Acquire a lock for a given duration and release it after action is executed.
   *
   * @param <T>        type od value emitted by the action
   * @param duration   how much time must pass for the acquired lock to expire
   * @param onAcquired executed when lock is acquired
   * @return items from onAcquired if lock was acquired, otherwise Flux.empty()
   * @see ReactorDistributedLock#acquire(Duration)
   */
  default <T> Flux<T> acquireAndExecute(Duration duration, Flux<T> onAcquired) {
    return executeOnAcquired(acquire(duration), onAcquired, this::release);
  }

  /**
   * Acquire a lock without expiration time and release it after action is executed.
   *
   * @param <T>        type od value emitted by the action
   * @param onAcquired executed when lock is acquired
   * @return items from onAcquired if lock was acquired, otherwise Mono.empty()
   * @see ReactorDistributedLock#acquireForever()
   */
  default <T> Mono<T> acquireForeverAndExecute(Mono<T> onAcquired) {
    return executeOnAcquired(acquireForever(), onAcquired, this::release);
  }

  /**
   * Acquire a lock without expiration time and release it after action is executed.
   *
   * @param <T>        type od value emitted by the action
   * @param onAcquired executed when lock is acquired
   * @return items from onAcquired if lock was acquired, otherwise Flux.empty()
   * @see ReactorDistributedLock#acquireForever()
   */
  default <T> Flux<T> acquireForeverAndExecute(Flux<T> onAcquired) {
    return executeOnAcquired(acquireForever(), onAcquired, this::release);
  }

  /**
   * Run the action when lock is released
   *
   * @param <T>        type od value emitted by the action
   * @param onReleased to be executed subscribed to when lock is released
   * @return items from onReleased, or Flux.empty()
   * @see ReactorDistributedLock#release()
   */
  default <T> Mono<T> releaseAndExecute(Mono<T> onReleased) {
    return executeOnReleased(release(), onReleased);
  }

  /**
   * Run the action when lock is released
   *
   * @param <T>        type od value emitted by the action
   * @param onReleased executed when lock is released
   * @return items from onReleased, or Flux.empty()
   * @see ReactorDistributedLock#release()
   */
  default <T> Flux<T> releaseAndExecute(Flux<T> onReleased) {
    return executeOnReleased(release(), onReleased);
  }
}
