package com.coditory.distributed.lock.common;

import java.time.Duration;

public final class DistributedLockDefaults {
  public static final Duration DEFAULT_LOCK_DURATION = Duration.ofMinutes(5);
  public static final InstanceId DEFAULT_INSTANCE_ID = InstanceId.uniqueInstanceId();

  private DistributedLockDefaults() {
    throw new IllegalStateException("Do not instantiate utility class");
  }
}
