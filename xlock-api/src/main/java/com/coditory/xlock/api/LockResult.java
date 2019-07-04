package com.coditory.xlock.api;

public interface LockResult {
  LockResult onLockGranted(Runnable runnable);
  LockResult onLockRefused(Runnable runnable);
}
