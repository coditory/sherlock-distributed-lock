package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;

import java.util.concurrent.Flow.Publisher;

interface ReactiveDistributedLockConnector {
  /**
   * Initializes underlying infrastructure for locks. Most frequently triggers database index
   * creation.
   * <p>
   * If it is not executed explicitly, connector may execute it during first acquire acquisition or
   * release.
   */
  Publisher<InitializationResult> initialize();

  /**
   * Acquires a acquire when there is no acquire acquired with the same lockId.
   */
  Publisher<AcquireResult> acquire(LockRequest lockRequest);

  /**
   * Acquires a acquire when there is no acquire acquired with the same lockId. Prolongs the acquire
   * if it was already acquired by the same instance.
   */
  Publisher<AcquireResult> acquireOrProlong(LockRequest lockRequest);

  /**
   * Acquires a acquire even if it was already acquired.
   */
  Publisher<AcquireResult> forceAcquire(LockRequest lockRequest);

  /**
   * Unlock previously acquired acquire by the same instance.
   */
  Publisher<ReleaseResult> release(LockId lockId, OwnerId ownerId);

  /**
   * Releases a lock without checking its owner or release date.
   */
  Publisher<ReleaseResult> forceRelease(LockId lockId);

  /**
   * Releases all locks without checking their owners or release dates.
   */
  Publisher<ReleaseResult> forceReleaseAll();
}
