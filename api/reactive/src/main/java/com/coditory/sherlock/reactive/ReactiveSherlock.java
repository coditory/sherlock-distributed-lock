package com.coditory.sherlock.reactive;

import java.time.Duration;
import java.util.function.Function;

public interface ReactiveSherlock {
  String getInstanceId();

  Duration getLockDuration();

  ReactiveDistributedLock createReentrantLock(String lockId);

  ReactiveDistributedLock createReentrantLock(String lockId, Duration duration);

  ReactiveDistributedLock createLock(String lockId);

  ReactiveDistributedLock createLock(String lockId, Duration duration);

  ReactiveDistributedLock createOverridingLock(String lockId);

  ReactiveDistributedLock createOverridingLock(String lockId, Duration duration);

  default <T> T map(Function<ReactiveSherlock, T> mapper) {
    return mapper.apply(this);
  }
}
