package com.coditory.sherlock;

import java.time.Clock;
import java.time.Duration;

public final class SherlockDefaults {
    public static final Duration DEFAULT_LOCK_DURATION = Duration.ofMinutes(5);
    public static final Clock DEFAULT_CLOCK = Clock.systemUTC();
    public static final String DEFAULT_MIGRATOR_LOCK_ID = "migrator";

    private SherlockDefaults() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }
}
