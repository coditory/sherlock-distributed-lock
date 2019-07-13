package com.coditory.sherlock;

import java.time.Duration;

import static com.coditory.sherlock.DistributedLockExecutor.executeOnAcquired;
import static com.coditory.sherlock.DistributedLockExecutor.executeOnReleased;

/**
 * A lock for distributed environment consisting of multiple application instances. Acquire a
 * distributed lock when only one application instance should execute a specific action.
 *
 * @see Sherlock
 */
public interface DistributedLock {
  /**
   * Return the lock id.
   *
   * @return the lock id
   */
  String getId();

  /**
   * Try to acquire the lock. Lock is acquired for a configured duration.
   *
   * @return true, if lock was acquired
   */
  boolean acquire();

  /**
   * Try to acquire the lock for a given duration.
   *
   * @param duration how much time must pass for the acquired lock to expire
   * @return true, if lock was acquired
   */
  boolean acquire(Duration duration);

  /**
   * Try to acquire the lock without expiring date. It is potentially dangerous. Lookout for a
   * situation where the lock owning instance goes down with out releasing the lock.
   *
   * @return true, if lock was acquired
   */
  boolean acquireForever();

  /**
   * Release the lock
   *
   * @return true, if lock was released in this call. If lock could not be released or was released
   * by a different instance then false is returned.
   */
  boolean release();

  /**
   * Acquire a lock and release it after action is executed.
   *
   * @param action to be executed when lock is acquired
   * @return true if lock was acquired.
   * @see DistributedLock#acquire()
   */
  default boolean acquireAndExecute(Runnable action) {
    return executeOnAcquired(acquire(), action, this::release);
  }

  /**
   * Acquire a lock for a given duration and release it after action is executed.
   *
   * @param duration how much time must pass for the acquired lock to expire
   * @param action to be executed when lock is acquired
   * @return true, if lock was acquired
   * @see DistributedLock#acquire(Duration)
   */
  default boolean acquireAndExecute(Duration duration, Runnable action) {
    return executeOnAcquired(acquire(duration), action, this::release);
  }

  /**
   * Acquire a lock without expiration time and release it after action is executed.
   *
   * @param action to be executed when lock is acquired
   * @return true, if lock was acquired
   * @see DistributedLock#acquireForever()
   */
  default boolean acquireForeverAndExecute(Runnable action) {
    return executeOnAcquired(acquireForever(), action, this::release);
  }

  /**
   * Run the action when lock is released
   *
   * @param action to be executed when lock is released
   * @return true, if lock was release
   * @see DistributedLock#release()
   */
  default boolean releaseAndExecute(Runnable action) {
    return executeOnReleased(release(), action);
  }
}
