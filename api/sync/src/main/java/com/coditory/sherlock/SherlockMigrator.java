package com.coditory.sherlock;

import com.coditory.sherlock.DistributedLock.AcquireAndExecuteResult;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * Migration mechanism based on {@link Sherlock} distributed locks.
 * <p>
 * It can be used to perform one way database migrations.
 * <p>
 * Migration rules:
 * <ul>
 * <li> migrations must not run in parallel
 * <li> migration change sets are applied in order
 * <li> migration change sets must be run only once per all migrations
 * <li> migration process stops after first change set failure
 * </ul>
 */
public final class SherlockMigrator {
    private static final String DEFAULT_MIGRATOR_LOCK_ID = "migrator";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<MigrationChangeSet> migrationChangeSets = new ArrayList<>();
    private final String migrationId;
    private final Sherlock sherlock;
    private final DistributedLock migrationLock;
    private final Set<String> migrationLockIds = new HashSet<>();

    /**
     * @param sherlock sherlock used to manage migration locks
     */
    public SherlockMigrator(@NotNull Sherlock sherlock) {
        this(DEFAULT_MIGRATOR_LOCK_ID, sherlock);
    }

    /**
     * @param migrationId id used as lock id for the whole migration process
     * @param sherlock    sherlock used to manage migration locks
     */
    public SherlockMigrator(@NotNull String migrationId, @NotNull Sherlock sherlock) {
        expectNonEmpty(migrationId, "migrationId");
        expectNonNull(sherlock, "sherlock");
        this.migrationId = migrationId;
        this.sherlock = sherlock;
        this.migrationLock = sherlock.createLock()
                .withLockId(migrationId)
                .withPermanentLockDuration()
                .withStaticUniqueOwnerId()
                .build();
        this.migrationLockIds.add(migrationId);
    }

    /**
     * Adds change set to migration process.
     *
     * @param changeSetId unique change set id used. This is is used as a lock id in migration
     *                    process.
     * @param changeSet   change set action that should be run if change set was not already applied
     * @return the migrator
     */
    @NotNull
    public SherlockMigrator addChangeSet(@NotNull String changeSetId, @NotNull Runnable changeSet) {
        expectNonEmpty(changeSetId, "changeSetId");
        expectNonNull(changeSet, "changeSet");
        ensureUniqueChangeSetId(changeSetId);
        migrationLockIds.add(changeSetId);
        DistributedLock changeSetLock = createChangeSetLock(changeSetId);
        migrationChangeSets.add(new MigrationChangeSet(changeSetId, changeSetLock, changeSet));
        return this;
    }

    private DistributedLock createChangeSetLock(String migrationId) {
        return sherlock.createLock()
                .withLockId(migrationId)
                .withPermanentLockDuration()
                .withStaticUniqueOwnerId()
                .build();
    }

    /**
     * Runs the migration process.
     *
     * @return migration result
     */
    @NotNull
    public MigrationResult migrate() {
        AcquireAndExecuteResult acquireResult = migrationLock.acquireAndExecute(this::runMigrations);
        return new MigrationResult(acquireResult.isAcquired());
    }

    private void runMigrations() {
        Timer timer = Timer.start();
        logger.info("Starting migration: {}", migrationId);
        migrationChangeSets.forEach(MigrationChangeSet::execute);
        logger.info("Migration finished successfully: {} [{}]", migrationId, timer.elapsed());
    }

    private void ensureUniqueChangeSetId(String changeSetId) {
        if (migrationLockIds.contains(changeSetId)) {
            throw new IllegalArgumentException(
                    "Expected unique change set ids. Duplicated id: " + changeSetId);
        }
    }

    private static class MigrationChangeSet {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        private final String id;
        private final DistributedLock lock;
        private final Runnable action;

        MigrationChangeSet(String id, DistributedLock lock, Runnable action) {
            this.id = id;
            this.lock = lock;
            this.action = action;
        }

        void execute() {
            Timer timer = Timer.start();
            if (lock.acquire()) {
                logger.debug("Executing migration change set: {}", id);
                try {
                    action.run();
                    logger.info("Migration change set applied: {} [{}]", id, timer.elapsed());
                } catch (Throwable exception) {
                    logger.warn(
                            "Migration change set failure: {} [{}]. Stopping migration process. Fix problem and rerun the migration.",
                            id, timer.elapsed(), exception);
                    lock.release();
                    throw exception;
                }
            } else {
                logger.info("Migration change set skipped: {}", id);
            }
        }
    }

    public static final class MigrationResult {
        private final boolean migrated;

        MigrationResult(boolean migrated) {
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
}
