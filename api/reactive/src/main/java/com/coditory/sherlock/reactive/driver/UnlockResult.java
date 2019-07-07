package com.coditory.sherlock.reactive.driver;

import java.util.Objects;

public class UnlockResult {
  private static final UnlockResult SUCCESS = new UnlockResult(true);
  private static final UnlockResult FAILURE = new UnlockResult(false);

  public static UnlockResult of(boolean value) {
    return value ? SUCCESS : FAILURE;
  }

  private boolean unlocked;

  private UnlockResult(boolean unlocked) {
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
