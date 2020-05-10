package com.coditory.sherlock;

interface DistributedLockConnector {
  void initialize();

  boolean acquire(LockRequest lockRequest);

  boolean acquireOrProlong(LockRequest lockRequest);

  boolean forceAcquire(LockRequest lockRequest);

  boolean release(LockId lockId, OwnerId ownerId);

  boolean forceRelease(LockId lockId);

  boolean forceReleaseAll();

  LockState getLockState(LockId lockId, OwnerId ownerId);
}
