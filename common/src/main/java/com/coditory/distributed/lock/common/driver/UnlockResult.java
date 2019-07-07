package com.coditory.distributed.lock.common.driver;

import java.util.Objects;

public class UnlockResult {
  private boolean unlocked;

  public UnlockResult(boolean unlocked) {
    this.unlocked = unlocked;
  }

  public boolean isUnlocked() {
    return unlocked;
  }

  @Override
  public String toString() {
    return "UnlockResult{unlocked=" + unlocked + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UnlockResult that = (UnlockResult) o;
    return unlocked == that.unlocked;
  }

  @Override
  public int hashCode() {
    return Objects.hash(unlocked);
  }
}
