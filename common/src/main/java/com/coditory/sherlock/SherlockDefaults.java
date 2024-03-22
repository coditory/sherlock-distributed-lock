package com.coditory.sherlock;

import java.time.Clock;
import java.time.Duration;

public final class SherlockDefaults {
    public static final LockDuration DEFAULT_LOCK_DURATION = LockDuration.of(Duration.ofMinutes(5));
    public static final OwnerIdPolicy DEFAULT_OWNER_ID_POLICY = OwnerIdPolicy.uniqueOwnerIdPolicy();
    public static final Clock DEFAULT_CLOCK = Clock.systemUTC();
    public static final String DEFAULT_MIGRATOR_LOCK_ID = "migrator";

    private SherlockDefaults() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }
}
