package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Supplier;

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
   *   has expired or was released earlier  then {@link ReleaseResult#FAILURE} is returned.
   */
  Mono<ReleaseResult> release();

  /**
   * Acquire a lock and release it after action is executed or fails.
   *
   * @param <T> type od value emitted by the action
   * @param mono to be executed subscribed to when lock is acquired
   * @return true if lock is acquired.
   * @see ReactorDistributedLock#acquire()
   */
  default <T> Mono<T> acquireAndExecute(Mono<T> mono) {
    return ReactorDistributedLockExecutor.executeOnAcquired(acquire(), mono, this::release);
  }

  /**
   * Acquire a lock for a given duration and release it after action is executed.
   *
   * @param <T> type od value emitted by the action
   * @param duration how much time must pass for the acquired lock to expire
   * @param mono to be executed subscribed to when lock is acquired
   * @return true, if lock is acquired
   * @see ReactorDistributedLock#acquire(Duration)
   */
  default <T> Mono<T> acquireAndExecute(Duration duration, Mono<T> mono) {
    return ReactorDistributedLockExecutor
      .executeOnAcquired(acquire(duration), mono, this::release);
  }

  /**
   * Acquire a lock without expiration time and release it after action is executed.
   *
   * @param <T> type od value emitted by the action
   * @param mono to be executed subscribed to when lock is acquired
   * @return true, if lock is acquired
   * @see ReactorDistributedLock#acquireForever()
   */
  default <T> Mono<T> acquireForeverAndExecute(Mono<T> mono) {
    return ReactorDistributedLockExecutor
      .executeOnAcquired(acquireForever(), mono, this::release);
  }

  /**
   * Run the action when lock is released
   *
   * @param <T> type od value emitted by the action
   * @param mono to be executed subscribed to when lock is released
   * @return true, if lock was release
   * @see ReactorDistributedLock#release()
   */
  default <T> Mono<T> releaseAndExecute(Mono<T> mono) {
    return ReactorDistributedLockExecutor.executeOnReleased(release(), mono);
  }
}
