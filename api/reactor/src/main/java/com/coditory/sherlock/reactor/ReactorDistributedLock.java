package com.coditory.sherlock.reactor;

import com.coditory.sherlock.reactive.driver.LockResult;
import com.coditory.sherlock.reactive.driver.ReleaseResult;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Supplier;

import static com.coditory.sherlock.reactor.ReactorDistributedLockExecutor.executeOnAcquired;
import static com.coditory.sherlock.reactor.ReactorDistributedLockExecutor.executeOnReleased;

/**
 * A lock for distributed environment consisting of multiple application instances. Acquire a
 * distributed lock when only one application instance should execute a specific action.
 *
 * @see ReactorSherlock
 */
public interface ReactorDistributedLock {
  /**
   * @return the lock id
   */
  String getId();

  /**
   * Try to acquire the lock. Lock is acquired for a configured duration. After that times it
   * expires and is ready to be acquired by other instance.
   *
   * @return {@link LockResult#SUCCESS}, if lock was acquired
   */
  Mono<LockResult> acquire();

  /**
   * Try to acquire the lock for a given duration. After that times it expires and is ready to be
   * acquired by other instance.
   *
   * @param duration - how much time must pass for the acquired lock to expire
   * @return {@link LockResult#SUCCESS}, if lock was acquired
   */
  Mono<LockResult> acquire(Duration duration);

  /**
   * Try to acquire the lock without expiring date. It is potentially dangerous. Lookout for a
   * situation where the lock owning instance goes down with out releasing the lock.
   *
   * @return {@link LockResult#SUCCESS}, if lock was acquired
   */
  Mono<LockResult> acquireForever();

  /**
   * Release the lock
   *
   * @return {@link ReleaseResult#SUCCESS}, if lock was released in this call. If lock could not be
   * released or was released by a different instance then {@link ReleaseResult#FAILURE} is
   * returned.
   */
  Mono<ReleaseResult> release();

  /**
   * Acquire a lock and release it after action is executed.
   *
   * @param action - to be executed when lock is acquired
   * @return true if lock was acquired.
   * @see ReactorDistributedLock#acquire()}
   */
  default <T> Mono<T> acquireAndExecute(Supplier<Mono<T>> action) {
    return executeOnAcquired(acquire(), action, this::release);
  }

  /**
   * Acquire a lock for a given duration and release it after action is executed.
   *
   * @param duration - how much time must pass for the acquired lock to expire
   * @param action - to be executed when lock is acquired
   * @return true, if lock was acquired
   * @see ReactorDistributedLock#acquire(Duration)}
   */
  default <T> Mono<T> acquireAndExecute(Duration duration, Supplier<Mono<T>> action) {
    return executeOnAcquired(acquire(duration), action, this::release);
  }

  /**
   * Acquire a lock without expiration time and release it after action is executed.
   *
   * @param action - to be executed when lock is acquired
   * @return true, if lock was acquired
   * @see ReactorDistributedLock#acquireForever()
   */
  default <T> Mono<T> acquireForeverAndExecute(Supplier<Mono<T>> action) {
    return executeOnAcquired(acquireForever(), action, this::release);
  }

  /**
   * Run the action when lock is released
   *
   * @param action - to be executed when lock is released
   * @return true, if lock was release
   * @see ReactorDistributedLock#release()
   */
  default <T> Mono<T> releaseAndExecute(Supplier<Mono<T>> action) {
    return executeOnReleased(release(), action);
  }
}
