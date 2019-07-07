package com.coditory.distributed.lock.reactive;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;
import com.coditory.distributed.lock.common.LockRequest;
import com.coditory.distributed.lock.common.driver.InitializationResult;
import com.coditory.distributed.lock.common.driver.LockResult;
import com.coditory.distributed.lock.common.driver.UnlockResult;

import java.util.concurrent.Flow.Publisher;

public interface ReactiveDistributedLockDriver {
  /**
   * Initializes underlying infrastructure for locks. Most frequently triggers database index
   * creation.
   *
   * If it is not executed explicitly, driver may execute it during first lock acquisition or
   * release.
   */
  Publisher<InitializationResult> initialize();

  /**
   * Acquires a lock when there is no lock acquired with the same lockId.
   */
  Publisher<LockResult> lock(LockRequest lockRequest);

  /**
   * Acquires a lock when there is no lock acquired with the same lockId. Prolongs the lock if it
   * was already acquired by the same instance.
   */
  Publisher<LockResult> lockOrRelock(LockRequest lockRequest);

  /**
   * Acquires a lock even if it was already acquired.
   */
  Publisher<LockResult> forceLock(LockRequest lockRequest);

  /**
   * Unlock previously acquired lock by the same instance.
   */
  Publisher<UnlockResult> unlock(LockId lockId, InstanceId instanceId);

  /**
   * Unlocks a lock without checking its owner or release date.
   */
  Publisher<UnlockResult> forceUnlock(LockId lockId);

  /**
   * Unlocks all previously acquired locks with out checking their state.
   */
  Publisher<UnlockResult> forceUnlockAll();
}
