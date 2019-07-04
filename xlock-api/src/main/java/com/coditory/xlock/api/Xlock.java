package com.coditory.xlock.api;

public interface Xlock {
  LockResult lock();
  LockResult unlock();
}
