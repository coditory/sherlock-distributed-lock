package com.coditory.distributed.lock;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;
import com.coditory.distributed.lock.common.LockRequest;

public interface DistributedLockDriver {
  /**
   * Initializes underlying infrastructure for locks. Most frequently triggers database index
   * creation.
   *
   * If it is not executed explicitly, driver may execute it during first acquire acquisition or
   * release.
   */
  void initialize();

  /**
   * Acquires a acquire when there is no acquire acquired with the same lockId.
   *
   * @return boolean - true if acquire was acquired by this call
   */
  boolean acquire(LockRequest lockRequest);

  /**
   * Acquires a acquire when there is no acquire acquired with the same lockId. Prolongs the acquire if it
   * was already acquired by the same instance.
   *
   * @return boolean - true if acquire was acquired by this call
   */
  boolean acquireOrProlong(LockRequest lockRequest);

  /**
   * Acquires a acquire even if it was already acquired.
   *
   * @return boolean - true if acquire was acquired by this call
   */
  boolean forceAcquire(LockRequest lockRequest);

  /**
   * Unlock previously acquired acquire by the same instance.
   *
   * @return boolean - true if acquire was released by this call
   */
  boolean release(LockId lockId, InstanceId instanceId);

  /**
   * Unlocks a acquire without checking its owner or release date.
   *
   * @return boolean - true if acquire was released by this call
   */
  boolean forceRelease(LockId lockId);

  /**
   * Unlocks all previously acquired locks with out checking their state.
   */
  void forceReleaseAll();
}
