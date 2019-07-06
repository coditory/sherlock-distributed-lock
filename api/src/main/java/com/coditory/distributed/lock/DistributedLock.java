package com.coditory.distributed.lock;

import java.time.Duration;

import static com.coditory.distributed.lock.LockActionExecutor.executeAndUnlock;

public interface DistributedLock {
  String getId();

  boolean lock();

  boolean lock(Duration duration);

  boolean lockInfinitely();

  boolean unlock();

  default boolean lockAndExecute(Runnable onAcquired) {
    return executeAndUnlock(lock(), onAcquired, this::unlock);
  }

  default boolean lockAndExecute(Duration duration, Runnable action) {
    return executeAndUnlock(lock(duration), action, this::unlock);
  }

  default boolean lockInfinitelyAndExecute(Runnable action) {
    return executeAndUnlock(lockInfinitely(), action, this::unlock);
  }

  default boolean unlockAndExecute(Runnable action) {
    boolean unlocked = unlock();
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
