package com.coditory.xlock.common.driver;

import com.coditory.xlock.common.InstanceId;
import com.coditory.xlock.common.LockId;

public interface DistributedLockDriver {
  /**
   * Initializes underlying infrastructure for locks.
   * Most frequently creates database indexes.
   *
   * If initialize is not executed explicite driver may execute it during first lock/unlock.
   */
  void initialize();

  /**
   * Acquires a lock when:
   * - there is no acquired lock with the same lockId
   *
   * @param lockRequest
   * @return boolean - true if lock was acquired by this invocation
   */
  boolean lock(LockRequest lockRequest);

  /**
   * Acquires a lock when:
   * - there is no acquired lock with the same lockId
   * - or acquired lock holder is same as instanceId
   *
   * @param lockRequest
   * @return boolean - true if lock was acquired by this invocation
   */
  boolean lockOrRelock(LockRequest lockRequest);

  /**
   * Acquires a lock even if it is already acquired.
   *
   * @param lockRequest
   * @return boolean - true if lock was acquired by this invocation
   */
  boolean forceLock(LockRequest lockRequest);

  /**
   * Unlock previously acquired lock when lockInstanceIds match.
   *
   * @return boolean - true if lock was released by this invocation
   */
  boolean unlock(LockId lockId, InstanceId instanceId);

  /**
   * Unlocks previously acquired lock with out checking release date.
   *
   * @return boolean - true if lock was released by this invocation
   */
  boolean forceUnlock(LockId lockId);

  /**
   * Unlocks all previously acquired locks with out checking their state.
   */
  void forceUnlockAll();
}
