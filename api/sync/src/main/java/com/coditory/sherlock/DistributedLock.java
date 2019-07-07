package com.coditory.sherlock;

import java.time.Duration;

import static com.coditory.sherlock.LockActionExecutor.executeAndUnlock;

public interface DistributedLock {
  String getId();

  boolean acquire();

  boolean acquire(Duration duration);

  boolean acquireForever();

  boolean release();

  default boolean acquireAndExecute(Runnable onAcquired) {
    return executeAndUnlock(acquire(), onAcquired, this::release);
  }

  default boolean acquireAndExecute(Duration duration, Runnable action) {
    return executeAndUnlock(acquire(duration), action, this::release);
  }

  default boolean acquireForeverAndExecute(Runnable action) {
    return executeAndUnlock(acquireForever(), action, this::release);
  }

  default boolean releaseAndExecute(Runnable action) {
    boolean unlocked = release();
    if (unlocked) {
      action.run();
    }
    return unlocked;
  }
}

final class LockActionExecutor {
  static boolean executeAndUnlock(boolean locked, Runnable onAcquired, Runnable unlock) {
    if (!locked) {
      return false;
    }
    try {
      onAcquired.run();
    } finally {
      unlock.run();
    }
    return true;
  }
}
