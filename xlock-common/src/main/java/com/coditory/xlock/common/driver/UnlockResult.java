package com.coditory.xlock.common.driver;

import java.util.Objects;

public class UnlockResult {
  private static UnlockResult unlockedSuccessIstance = new UnlockResult(true);
  private static UnlockResult unlockedFailureInstance = new UnlockResult(false);

  public static UnlockResult unlockSuccess() {
    return unlockedSuccessIstance;
  }

  public static UnlockResult unlockFailure() {
    return unlockedFailureInstance;
  }

  private final boolean unlocked;

  UnlockResult(boolean unlocked) {
    this.unlocked = unlocked;
  }

  public boolean isUnlocked() {
    return unlocked;
  }

  @Override
  public String toString() {
    return "UnlockResult{" +
        "unlocked=" + unlocked +
        '}';
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
