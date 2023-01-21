package com.coditory.sherlock;

import java.time.Clock;
import java.time.Duration;

import static java.time.Clock.systemDefaultZone;

final class SherlockDefaults {
    public static final LockDuration DEFAULT_LOCK_DURATION = LockDuration.of(Duration.ofMinutes(5));
    public static final OwnerIdPolicy DEFAULT_OWNER_ID_POLICY = OwnerIdPolicy.uniqueOwnerIdPolicy();
    public static final Clock DEFAULT_CLOCK = systemDefaultZone();

    private SherlockDefaults() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }
}
