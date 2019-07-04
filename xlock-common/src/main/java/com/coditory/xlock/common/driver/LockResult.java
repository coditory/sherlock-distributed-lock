package com.coditory.xlock.common.driver;

import java.util.Objects;

public class LockResult {
  private static LockResult lockGrantedInstance = new LockResult(true);
  private static LockResult lockRefusedInstance = new LockResult(false);

  public static LockResult lockGranted() {
    return lockGrantedInstance;
  }

  public static LockResult lockRefused() {
    return lockRefusedInstance;
  }

  private final boolean lockGranted;

  private LockResult(boolean lockGranted) {
    this.lockGranted = lockGranted;
  }

  public boolean isLockGranted() {
    return lockGranted;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LockResult that = (LockResult) o;
    return lockGranted == that.lockGranted;
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockGranted);
  }
}
