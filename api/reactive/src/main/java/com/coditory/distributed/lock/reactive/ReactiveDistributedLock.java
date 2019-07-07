package com.coditory.distributed.lock.reactive;

import com.coditory.distributed.lock.common.driver.LockResult;
import com.coditory.distributed.lock.common.driver.UnlockResult;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

public interface ReactiveDistributedLock {
  String getId();

  Publisher<LockResult> lock();

  Publisher<LockResult> lock(Duration duration);

  Publisher<LockResult> lockInfinitely();

  Publisher<UnlockResult> unlock();
}
