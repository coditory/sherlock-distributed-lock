package com.coditory.distributed.lock.reactive;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;
import com.coditory.distributed.lock.common.LockRequest;
import com.coditory.distributed.lock.reactive.driver.InitializationResult;
import com.coditory.distributed.lock.reactive.driver.LockResult;
import com.coditory.distributed.lock.reactive.driver.UnlockResult;

import java.util.concurrent.Flow.Publisher;

interface ReactiveDistributedLockDriver {
  /**
   * Initializes underlying infrastructure for locks. Most frequently triggers database index
   * creation.
   *
   * If it is not executed explicitly, driver may execute it during first acquire acquisition or
   * release.
   */
  Publisher<InitializationResult> initialize();

  /**
   * Acquires a acquire when there is no acquire acquired with the same lockId.
   */
  Publisher<LockResult> acquire(LockRequest lockRequest);

  /**
   * Acquires a acquire when there is no acquire acquired with the same lockId. Prolongs the acquire
   * if it was already acquired by the same instance.
   */
  Publisher<LockResult> acquireOrProlong(LockRequest lockRequest);

  /**
   * Acquires a acquire even if it was already acquired.
   */
  Publisher<LockResult> forceAcquire(LockRequest lockRequest);

  /**
   * Unlock previously acquired acquire by the same instance.
   */
  Publisher<UnlockResult> release(LockId lockId, InstanceId instanceId);

  /**
   * Unlocks a acquire without checking its owner or release date.
   */
  Publisher<UnlockResult> forceRelease(LockId lockId);

  /**
   * Unlocks all previously acquired locks with out checking their state.
   */
  Publisher<UnlockResult> forceReleaseAll();
}
