package com.coditory.xlock.api;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

class CrossServiceTimeLimitedLock implements CrossServiceLock {
  private final CrossServiceLockOperations operations;

  CrossServiceTimeLimitedLock(CrossServiceLockOperations operations) {
    this.operations = expectNonNull(operations);
  }

  @Override
  public CrossServiceLockResult lock() {
    return operations.lockInfinitely();
  }

  @Override
  public CrossServiceLock newInstance() {
    return new CrossServiceTimeLimitedLock(operations.newInstance());
  }
}
