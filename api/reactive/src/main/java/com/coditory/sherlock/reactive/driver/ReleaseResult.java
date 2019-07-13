package com.coditory.sherlock.reactive.driver;

import java.util.Objects;

public class ReleaseResult {
  public static final ReleaseResult SUCCESS = new ReleaseResult(true);
  public static final ReleaseResult FAILURE = new ReleaseResult(false);

  public static ReleaseResult of(boolean value) {
    return value ? SUCCESS : FAILURE;
  }

  private boolean unlocked;

  private ReleaseResult(boolean unlocked) {
    this.unlocked = unlocked;
  }

  public boolean isUnlocked() {
    return unlocked;
  }

  @Override
  public String toString() {
    return "ReleaseResult{unlocked=" + unlocked + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReleaseResult that = (ReleaseResult) o;
    return unlocked == that.unlocked;
  }

  @Override
  public int hashCode() {
    return Objects.hash(unlocked);
  }
}
