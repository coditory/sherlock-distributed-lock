package com.coditory.distributed.lock;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;
import com.coditory.distributed.lock.common.LockRequest;

public interface DistributedLockDriver {
  /**
   * Initializes underlying infrastructure for locks. Most frequently triggers database index
   * creation.
   *
   * If it is not executed explicitly, driver may execute it during first lock acquisition or
   * release.
   */
  void initialize();

  /**
   * Acquires a lock when there is no lock acquired with the same lockId.
   *
   * @return boolean - true if lock was acquired by this call
   */
  boolean lock(LockRequest lockRequest);

  /**
   * Acquires a lock when there is no lock acquired with the same lockId. Prolongs the lock if it
   * was already acquired by the same instance.
   *
   * @return boolean - true if lock was acquired by this call
   */
  boolean lockOrRelock(LockRequest lockRequest);

  /**
   * Acquires a lock even if it was already acquired.
   *
   * @return boolean - true if lock was acquired by this call
   */
  boolean forceLock(LockRequest lockRequest);

  /**
   * Unlock previously acquired lock by the same instance.
   *
   * @return boolean - true if lock was released by this call
   */
  boolean unlock(LockId lockId, InstanceId instanceId);

  /**
   * Unlocks a lock without checking its owner or release date.
   *
   * @return boolean - true if lock was released by this call
   */
  boolean forceUnlock(LockId lockId);

  /**
   * Unlocks all previously acquired locks with out checking their state.
   */
  void forceUnlockAll();
}
