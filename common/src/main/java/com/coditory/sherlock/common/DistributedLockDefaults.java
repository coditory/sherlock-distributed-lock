package com.coditory.sherlock.common;

import java.time.Clock;
import java.time.Duration;

import static java.time.Clock.systemDefaultZone;

public final class DistributedLockDefaults {
  public static final Duration DEFAULT_LOCK_DURATION = Duration.ofMinutes(5);
  public static final InstanceId DEFAULT_INSTANCE_ID = InstanceId.uniqueInstanceId();
  public static final Clock DEFAULT_CLOCK = systemDefaultZone();
  public static final String DEFAULT_DB_TABLE_NAME = "locks";

  private DistributedLockDefaults() {
    throw new IllegalStateException("Do not instantiate utility class");
  }
}
