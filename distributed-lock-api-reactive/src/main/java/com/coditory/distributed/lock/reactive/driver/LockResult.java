package com.coditory.distributed.lock.reactive.driver;

public class LockResult {
  private boolean locked;

  public LockResult(boolean locked) {
    this.locked = locked;
  }
}
