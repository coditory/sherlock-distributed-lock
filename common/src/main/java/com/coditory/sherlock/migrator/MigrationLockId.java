package com.coditory.sherlock.migrator;

public final class MigrationLockId {
    public static final String DEFAULT_MIGRATOR_LOCK_ID = "migrator";

    MigrationLockId() {
        throw new IllegalArgumentException("Don't instantiate");
    }
}
