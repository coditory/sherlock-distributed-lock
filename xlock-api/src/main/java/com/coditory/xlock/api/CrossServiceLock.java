package com.coditory.xlock.api;

public interface CrossServiceLock {
  CrossServiceLockResult lock();

  CrossServiceLock newInstance();
}
