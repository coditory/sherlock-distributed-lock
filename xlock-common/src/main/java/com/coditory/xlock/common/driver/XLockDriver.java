package com.coditory.xlock.common.driver;

import com.coditory.xlock.common.AcquisitionId;
import com.coditory.xlock.common.InstanceId;
import com.coditory.xlock.common.LockId;
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
   * Unlock previously acquired lock.
   * Unlocks fails when lock was not acquired before.
   *
   * @return UnlockResult
   */
  UnlockResult unlock(LockId lockId);

  /**
   * Unlock previously acquired lock when lockAcquisitionIds match.
   * Unlocks fails when acquisitionId is not registered.
   *
   * @return UnlockResult
   */
  UnlockResult unlock(AcquisitionId acquisitionId);

  /**
   * Unlocks previously acquired lock with out checking release date.
   *
   * @return UnlockResult
   */
  UnlockResult forceUnlock(LockId lockId);

  /**
   * Unlocks previously acquired lock with out checking release date.
   *
   * @return UnlockResult
   */
  UnlockResult forceUnlock(AcquisitionId acquisitionId);

  /**
   * Retrieves a lock state.
   *
   * @return Optional<LockState> - optional is empty when lock was not acquired or it was unlocked manually
   */
  Optional<LockState> getLockState(LockId lockId);
}
