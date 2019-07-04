package com.coditory.xlock.api;

public interface CrossServiceLockResult {
  boolean isLockGranted();

  CrossServiceLockResult onLockGranted(Runnable runnable);

  CrossServiceLockResult onLockRefused(Runnable runnable);

  void unlock();
}

class CrossServiceLockGranted implements CrossServiceLockResult {
  private final CrossServiceLockOperations lockOperations;

  public CrossServiceLockGranted(CrossServiceLockOperations lockOperations) {
    this.lockOperations = lockOperations;
  }

  public boolean isLockGranted() {
    return true;
  }

  public CrossServiceLockGranted onLockGranted(Runnable runnable) {
    runnable.run();
    return this;
  }

  public CrossServiceLockResult onLockRefused(Runnable runnable) {
    return this;
  }

  public void unlock() {
    lockOperations.unlock();
  }
}

class CrossServiceLockRefused implements CrossServiceLockResult {
  public boolean isLockGranted() {
    return false;
  }

  public CrossServiceLockRefused onLockGranted(Runnable runnable) {
    return this;
  }

  public CrossServiceLockRefused onLockRefused(Runnable runnable) {
    runnable.run();
    return this;
  }

  public void unlock() {
    // deliberately empty
  }
}
