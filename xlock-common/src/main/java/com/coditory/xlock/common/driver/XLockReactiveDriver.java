package com.coditory.xlock.common.driver;

import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.LockState;

import java.util.concurrent.Flow.Publisher;

public interface XLockReactiveDriver {
  Publisher<LockResult> lock(LockRequest lockRequest);
  Publisher<LockResult> forceLock(LockRequest forceLockRequest);
  Publisher<UnlockResult> unlock(UnlockRequest unlockRequest);
  Publisher<UnlockResult> forceUnlock(UnlockRequest unlockRequest);
  Publisher<LockState> getLockState(LockId lockId);
}
