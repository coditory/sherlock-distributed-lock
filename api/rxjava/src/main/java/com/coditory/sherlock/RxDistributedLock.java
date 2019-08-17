package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.time.Duration;

/**
 * A reactive distributed lock with RxJava API.
 *
 * @see RxSherlock
 */
public interface RxDistributedLock {
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
  Single<AcquireResult> acquire();

  /**
   * Try to acquire the lock for a given duration.
   *
   * @param duration how much time must pass for the acquired lock to expire
   * @return {@link AcquireResult#SUCCESS} if lock is acquired
   */
  Single<AcquireResult> acquire(Duration duration);

  /**
   * Try to acquire the lock without expiring date.
   * <p>
   * It is potentially dangerous. Lookout for a situation when the lock owning instance goes down
   * with out releasing the lock.
   *
   * @return {@link AcquireResult#SUCCESS} if lock is acquired
   */
  Single<AcquireResult> acquireForever();

  /**
   * Try to release the lock.
   *
   * @return {@link ReleaseResult#SUCCESS} if lock was released by this method invocation. If lock
   *   has expired or was released earlier  then {@link ReleaseResult#FAILURE} is returned.
   */
  Single<ReleaseResult> release();

  /**
   * Acquire a lock and release it after action is executed.
   *
   * @param <T> type od value emitted by the action
   * @param single to be subscribed to when lock is acquired
   * @return true if lock is acquired.
   * @see RxDistributedLock#acquire()
   */
  default <T> Maybe<T> acquireAndExecute(Single<T> single) {
    return RxDistributedLockExecutor.executeOnAcquired(acquire(), single, this::release);
  }

  /**
   * Acquire a lock for a given duration and release it after action is executed.
   *
   * @param <T> type od value emitted by the action
   * @param duration how much time must pass for the acquired lock to expire
   * @param single to be subscribed to when lock is acquired
   * @return true, if lock is acquired
   * @see RxDistributedLock#acquire(Duration)
   */
  default <T> Maybe<T> acquireAndExecute(Duration duration, Single<T> single) {
    return RxDistributedLockExecutor.executeOnAcquired(acquire(duration), single, this::release);
  }

  /**
   * Acquire a lock without expiration time and release it after action is executed.
   *
   * @param <T> type od value emitted by the action
   * @param single to be subscribed to when lock is acquired
   * @return true, if lock is acquired
   * @see RxDistributedLock#acquireForever()
   */
  default <T> Maybe<T> acquireForeverAndExecute(Single<T> single) {
    return RxDistributedLockExecutor.executeOnAcquired(acquireForever(), single, this::release);
  }

  /**
   * Run the action when lock is released
   *
   * @param <T> type od value emitted by the action
   * @param single to be subscribed to when lock is released
   * @return true, if lock was release
   * @see RxDistributedLock#release()
   */
  default <T> Maybe<T> releaseAndExecute(Single<T> single) {
    return RxDistributedLockExecutor.executeOnReleased(release(), single);
  }
}
