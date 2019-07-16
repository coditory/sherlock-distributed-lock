package com.coditory.sherlock.common;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.common.OwnerIdGenerator.RANDOM_OWNER_ID_GENERATOR;
import static java.time.Clock.systemDefaultZone;

public final class SherlockDefaults {
  public static final LockDuration DEFAULT_LOCK_DURATION = LockDuration.of(Duration.ofMinutes(5));
  public static final OwnerId DEFAULT_OWNER_ID = OwnerId.uniqueOwnerId();
  public static final OwnerIdGenerator DEFAULT_OWNER_ID_GENERATOR = RANDOM_OWNER_ID_GENERATOR;
  public static final Clock DEFAULT_CLOCK = systemDefaultZone();

  private SherlockDefaults() {
    throw new IllegalStateException("Do not instantiate utility class");
  }
}
