package com.coditory.xlock.api;

public interface UnlockResult {
  UnlockResult onUnlocked(Runnable runnable);
  UnlockResult onAlreadyUnlocked(Runnable runnable);
}
