package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;

import java.util.concurrent.Flow.Publisher;

interface ReactiveDistributedLockConnector {
  Publisher<InitializationResult> initialize();

  Publisher<AcquireResult> acquire(LockRequest lockRequest);

  Publisher<AcquireResult> acquireOrProlong(LockRequest lockRequest);

  Publisher<AcquireResult> forceAcquire(LockRequest lockRequest);

  Publisher<ReleaseResult> release(LockId lockId, OwnerId ownerId);

  Publisher<ReleaseResult> forceRelease(LockId lockId);

  Publisher<ReleaseResult> forceReleaseAll();

  Publisher<LockState> getLockState(LockId lockId, OwnerId ownerId);
}
