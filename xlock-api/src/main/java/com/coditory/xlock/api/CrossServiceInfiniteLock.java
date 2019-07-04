package com.coditory.xlock.api;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

class CrossServiceInfiniteLock implements CrossServiceLock {
  private final CrossServiceLockOperations operations;

  CrossServiceInfiniteLock(CrossServiceLockOperations operations) {
    this.operations = expectNonNull(operations);
  }

  @Override
  public CrossServiceLockResult lock() {
    return operations.lock();
  }

  @Override
  public CrossServiceLock newInstance() {
    return new CrossServiceInfiniteLock(operations.newInstance());
  }
}
