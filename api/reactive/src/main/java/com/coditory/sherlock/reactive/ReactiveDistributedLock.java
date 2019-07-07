package com.coditory.sherlock.reactive;

import com.coditory.sherlock.reactive.driver.LockResult;
import com.coditory.sherlock.reactive.driver.UnlockResult;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

public interface ReactiveDistributedLock {
  String getId();

  Publisher<LockResult> acquire();

  Publisher<LockResult> acquire(Duration duration);

  Publisher<LockResult> acquireForever();

  Publisher<UnlockResult> release();
}
