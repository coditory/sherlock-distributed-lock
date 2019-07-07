package com.coditory.distributed.lock.common.driver;

import java.util.Objects;

public class LockResult {
  private static final LockResult SUCCESS = new LockResult(true);
  private static final LockResult FAILURE = new LockResult(false);

  public static LockResult of(boolean value) {
    return value ? SUCCESS : FAILURE;
  }

  private boolean locked;

  private LockResult(boolean locked) {
    this.locked = locked;
  }

  public boolean isLocked() {
    return locked;
  }

  @Override
  public String toString() {
    return "LockResult{locked=" + locked + '}';
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
    return locked == that.locked;
  }

  @Override
  public int hashCode() {
    return Objects.hash(locked);
  }
}
