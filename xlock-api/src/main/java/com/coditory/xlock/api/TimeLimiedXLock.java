package com.coditory.xlock.api;

import java.time.Duration;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonEmpty;
import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

public class TimeLimiedXLock implements Xlock {
  private final String lockId;
  private final Duration duration;

  public TimeLimiedXLock(String lockId, Duration duration) {
    this.lockId = expectNonEmpty(lockId, "Expected non empty lockId");
    this.duration = expectNonNull(duration, "Expected non null duration");
  }

  @Override
  public LockResult lock() {
    return null;
  }

  public LockResult lock(Duration duration) {
    return null;
  }

  @Override
  public LockResult unlock() {
    return null;
  }
}
