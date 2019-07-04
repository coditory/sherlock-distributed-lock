package com.coditory.xlock.common.driver;

import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.LockInstanceId;
import com.coditory.xlock.common.LockState;

import java.util.Optional;

public interface XLockDriver {
  /**
   * Tries to acquire a lock.
   * Lock is acquired when there is no previous lock
   * or previous lock has expired.
   *
   * @param lockRequest
   * @return LockResult
   */
  LockResult lock(LockRequest lockRequest);

  /**
   * Acquires a lock overriding previous acquisition.
   *
   * @param lockRequest
   * @return LockResult
   */
  LockResult forceLock(LockRequest lockRequest);

  /**
   * Unlock previously acquired lock when lockInstanceIds match.
   * Unlocks fails when acquisitionId is not registered.
   *
   * @return UnlockResult
   */
  UnlockResult unlock(LockInstanceId lockInstanceId);

  /**
   * Unlocks previously acquired lock with out checking release date.
   *
   * @return UnlockResult
   */
  UnlockResult forceUnlock(LockId lockId);

  /**
   * Retrieves a lock state.
   *
   * @return Optional<LockState> - optional is empty when lock was not acquired or it was unlocked manually
   */
  Optional<LockState> getLockState(LockId lockId);

  /**
   * Prepares underlying storage for locks
   */
  void prepare();
}
