package com.coditory.distributed.lock.reactive.driver;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;
import com.coditory.distributed.lock.common.LockRequest;

import java.util.concurrent.Flow.Publisher;

public interface ReactiveDistributedLockDriver {
  /**
   * Initializes underlying infrastructure for locks.
   * Most frequently creates database indexes.
   *
   * If initialize is not executed explicite driver may execute it during first lock/unlock.
   */
  Publisher<InitializationResult> initialize();

  /**
   * Acquires a lock when:
   * - there is no acquired lock with the same lockId
   *
   * @param lockRequest
   * @return boolean - true if lock was acquired by this invocation
   */
  Publisher<LockResult> lock(LockRequest lockRequest);

  /**
   * Acquires a lock when:
   * - there is no acquired lock with the same lockId
   * - or acquired lock holder is same as instanceId
   *
   * @param lockRequest
   * @return boolean - true if lock was acquired by this invocation
   */
  Publisher<LockResult> lockOrRelock(LockRequest lockRequest);

  /**
   * Acquires a lock even if it is already acquired.
   *
   * @param lockRequest
   * @return boolean - true if lock was acquired by this invocation
   */
  Publisher<LockResult> forceLock(LockRequest lockRequest);

  /**
   * Unlock previously acquired lock when lockInstanceIds match.
   *
   * @return boolean - true if lock was released by this invocation
   */
  Publisher<UnlockResult> unlock(LockId lockId, InstanceId instanceId);

  /**
   * Unlocks previously acquired lock with out checking release date.
   *
   * @return boolean - true if lock was released by this invocation
   */
  Publisher<UnlockResult> forceUnlock(LockId lockId);

  /**
   * Unlocks all previously acquired locks with out checking their state.
   */
  Publisher<UnlockResult> forceUnlockAll();
}
