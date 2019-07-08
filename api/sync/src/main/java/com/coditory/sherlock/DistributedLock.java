package com.coditory.sherlock;

import java.time.Duration;

import static com.coditory.sherlock.DistributedLockExecutor.executeOnAcquired;
import static com.coditory.sherlock.DistributedLockExecutor.executeOnReleased;

public interface DistributedLock {
  String getId();

  boolean acquire();

  boolean acquire(Duration duration);

  boolean acquireForever();

  boolean release();

  default boolean acquireAndExecute(Runnable action) {
    return executeOnAcquired(acquire(), action, this::release);
  }

  default boolean acquireAndExecute(Duration duration, Runnable action) {
    return executeOnAcquired(acquire(duration), action, this::release);
  }

  default boolean acquireForeverAndExecute(Runnable action) {
    return executeOnAcquired(acquireForever(), action, this::release);
  }

  default boolean releaseAndExecute(Runnable action) {
    return executeOnReleased(release(), action);
  }
}
