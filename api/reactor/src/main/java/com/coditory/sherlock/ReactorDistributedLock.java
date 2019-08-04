package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * A lock for distributed environment consisting of multiple application instances. Acquire a
 * distributed lock when only one application instance should execute a specific action.
 *
 * @see ReactorSherlock
 */
public interface ReactorDistributedLock {
  /**
   * Return the lock id.
   *
   * @return the lock id
   */
  String getId();

  /**
   * Try to acquire the lock. Lock is acquired for a configured duration.
   *
   * @return {@link AcquireResult#SUCCESS}, if lock is acquired
   */
  Mono<AcquireResult> acquire();

  /**
   * Try to acquire the lock for a given duration.
   *
   * @param duration how much time must pass for the acquired lock to expire
   * @return {@link AcquireResult#SUCCESS}, if lock is acquired
   */
  Mono<AcquireResult> acquire(Duration duration);

  /**
   * Try to acquire the lock without expiring date. It is potentially dangerous. Lookout for a
   * situation where the lock owning instance goes down with out releasing the lock.
   *
   * @return {@link AcquireResult#SUCCESS}, if lock is acquired
   */
  Mono<AcquireResult> acquireForever();

  /**
   * Release the lock
   *
   * @return {@link ReleaseResult#SUCCESS}, if lock was released in this call. If lock could not be
   *     released or was released by a different instance then {@link ReleaseResult#FAILURE} is
   *     returned.
   */
  Mono<ReleaseResult> release();

  /**
   * Acquire a lock and release it after action is executed.
   *
   * @param <T> type od value emitted by the action
   * @param action to be executed when lock is acquired
   * @return true if lock is acquired.
   * @see ReactorDistributedLock#acquire()
   */
  default <T> Mono<T> acquireAndExecute(Supplier<Mono<T>> action) {
    return ReactorDistributedLockExecutor.executeOnAcquired(acquire(), action, this::release);
  }

  /**
   * Acquire a lock for a given duration and release it after action is executed.
   *
   * @param <T> type od value emitted by the action
   * @param duration how much time must pass for the acquired lock to expire
   * @param action to be executed when lock is acquired
   * @return true, if lock is acquired
   * @see ReactorDistributedLock#acquire(Duration)
   */
  default <T> Mono<T> acquireAndExecute(Duration duration, Supplier<Mono<T>> action) {
    return ReactorDistributedLockExecutor.executeOnAcquired(acquire(duration), action, this::release);
  }

  /**
   * Acquire a lock without expiration time and release it after action is executed.
   *
   * @param <T> type od value emitted by the action
   * @param action to be executed when lock is acquired
   * @return true, if lock is acquired
   * @see ReactorDistributedLock#acquireForever()
   */
  default <T> Mono<T> acquireForeverAndExecute(Supplier<Mono<T>> action) {
    return ReactorDistributedLockExecutor.executeOnAcquired(acquireForever(), action, this::release);
  }

  /**
   * Run the action when lock is released
   *
   * @param <T> type od value emitted by the action
   * @param action to be executed when lock is released
   * @return true, if lock was release
   * @see ReactorDistributedLock#release()
   */
  default <T> Mono<T> releaseAndExecute(Supplier<Mono<T>> action) {
    return ReactorDistributedLockExecutor.executeOnReleased(release(), action);
  }
}
