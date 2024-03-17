package com.coditory.sherlock.migrator;

import org.jetbrains.annotations.NotNull;

import static com.coditory.sherlock.Preconditions.expectNonNull;

public final class MigrationResult {
    // TODO: add fields: migration lock acquired, executed changesets
    private final boolean migrated;

    public MigrationResult(boolean migrated) {
        this.migrated = migrated;
    }

    public boolean isMigrated() {
        return migrated;
    }

    /**
     * Executes the action when migration process finishes. The action is only executed by the
     * migrator instance that started the migration process.
     *
     * @param action the action to be executed after migration
     * @return migration result for chaining
     */
    @NotNull
    public MigrationResult onFinish(@NotNull Runnable action) {
        expectNonNull(action, "action");
        if (migrated) {
            action.run();
        }
        return this;
    }

    /**
     * Executes the action when migration lock was not acquired.
     *
     * @param action the action to be executed when migration lock was not acquired
     * @return migration result for chaining
     */
    @NotNull
    public MigrationResult onRejected(@NotNull Runnable action) {
        expectNonNull(action, "action");
        if (!migrated) {
            action.run();
        }
        return this;
    }
}
