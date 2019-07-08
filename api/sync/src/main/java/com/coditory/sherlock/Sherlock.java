package com.coditory.sherlock;

import com.coditory.sherlock.DistributedLock;

import java.time.Duration;

public interface Sherlock {
  String getInstanceId();

  Duration getLockDuration();

  DistributedLock createReentrantLock(String lockId);

  DistributedLock createReentrantLock(String lockId, Duration duration);

  DistributedLock createLock(String lockId);

  DistributedLock createLock(String lockId, Duration duration);

  DistributedLock createOverridingLock(String lockId);

  DistributedLock createOverridingLock(String lockId, Duration duration);
}
