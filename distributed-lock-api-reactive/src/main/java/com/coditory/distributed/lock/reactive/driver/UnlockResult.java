package com.coditory.distributed.lock.reactive.driver;

public class UnlockResult {
  private boolean unlocked;

  public UnlockResult(boolean unlocked) {
    this.unlocked = unlocked;
  }
}
