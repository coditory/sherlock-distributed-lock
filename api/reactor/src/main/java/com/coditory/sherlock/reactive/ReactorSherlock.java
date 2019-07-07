package com.coditory.sherlock.reactive;

import java.time.Duration;

public interface ReactorSherlock {
  static ReactorSherlock reactorSherlock(ReactiveSherlock locks) {
    return new ReactorSherlockWithDriver(locks);
  }

  String getInstanceId();

  Duration getLockDuration();

  ReactorDistributedLock createReentrantLock(String lockId);

  ReactorDistributedLock createReentrantLock(String lockId, Duration duration);

  ReactorDistributedLock createLock(String lockId);

  ReactorDistributedLock createLock(String lockId, Duration duration);

  ReactorDistributedLock createOverridingLock(String lockId);

  ReactorDistributedLock createOverridingLock(String lockId, Duration duration);
}
