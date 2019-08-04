package com.coditory.sherlock;

import java.time.Duration;

/**
 * A lock for distributed environment consisting of multiple application instances.
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
   * @return true, if lock is acquired
   */
  boolean acquire();

  /**
   * Try to acquire the lock for a given duration.
   *
   * @param duration how much time must pass for the acquired lock to expire
   * @return true, if lock is acquired
   */
  boolean acquire(Duration duration);

  /**
   * Try to acquire the lock without expiring date. It is potentially dangerous. Lookout for a
   * situation where the lock owning instance goes down with out releasing the lock.
   *
   * @return true, if lock is acquired
   */
  boolean acquireForever();

  /**
   * Release the lock
   *
   * @return true, if lock was released in this call. If lock has expired or was released by a
   *     different instance then false is returned.
   */
  boolean release();

  /**
   * Acquire a lock and release it after action is executed.
   *
   * @param action to be executed when lock is acquired
   * @return true if lock is acquired.
   * @see DistributedLock#acquire()
   */
  default AcquireAndExecuteResult acquireAndExecute(Runnable action) {
    return AcquireAndExecuteResult
      .executeOnAcquired(acquire(), action, this::release);
  }

  default AcquireAndExecuteResult acquireAndExecute(Duration duration, Runnable action) {
    return AcquireAndExecuteResult
      .executeOnAcquired(acquire(duration), action, this::release);
  }

  default AcquireAndExecuteResult acquireForeverAndExecute(Runnable action) {
    return AcquireAndExecuteResult
      .executeOnAcquired(acquireForever(), action, this::release);
  }

  /**
   * Run the action when lock is released
   *
   * @param action to be executed when lock is released
   * @return true, if lock was release
   * @see DistributedLock#release()
   */
  default ReleaseAndExecuteResult releaseAndExecute(Runnable action) {
    return ReleaseAndExecuteResult
      .executeOnReleased(release(), action);
  }

  class AcquireAndExecuteResult {
    private static AcquireAndExecuteResult executeOnAcquired(boolean acquired, Runnable action, Runnable release) {
      if (acquired) {
        try {
          action.run();
        } finally {
          release.run();
        }
      }
      return new AcquireAndExecuteResult(acquired);
    }

    private final boolean acquired;

    AcquireAndExecuteResult(boolean acquired) {
      this.acquired = acquired;
    }

    boolean isAcquired() {
      return acquired;
    }

    AcquireAndExecuteResult onNotAcquired(Runnable action) {
      if (!acquired) {
        action.run();
      }
      return this;
    }
  }

  class ReleaseAndExecuteResult {
    private static ReleaseAndExecuteResult executeOnReleased(boolean released, Runnable action) {
      if (released) {
        action.run();
      }
      return new ReleaseAndExecuteResult(released);
    }

    private final boolean released;

    ReleaseAndExecuteResult(boolean released) {
      this.released = released;
    }

    boolean isReleased() {
      return released;
    }

    ReleaseAndExecuteResult onNotReleased(Runnable action) {
      if (!released) {
        action.run();
      }
      return this;
    }
  }
}
