package com.coditory.sherlock.migrator;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.Timer;
import com.coditory.sherlock.connector.AcquireResultWithValue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.coditory.sherlock.Preconditions.expectNonNull;

public final class SherlockMigrator {
    public static SherlockMigratorBuilder builder(@NotNull Sherlock sherlock) {
        return new SherlockMigratorBuilder(sherlock);
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<MigrationChangeSet> migrationChangeSets;
    private final DistributedLock migrationLock;

    SherlockMigrator(@NotNull DistributedLock migrationLock, @NotNull List<MigrationChangeSet> migrationChangeSets) {
        expectNonNull(migrationLock, "migrationLock");
        expectNonNull(migrationChangeSets, "migrationChangeSets");
        this.migrationLock = migrationLock;
        this.migrationChangeSets = List.copyOf(migrationChangeSets);
    }

    @NotNull
    public MigrationResult migrate() {
        AcquireResultWithValue<List<String>> acquireResult = migrationLock.runLocked(this::runMigrations);
        if (!acquireResult.acquired()) {
            logger.debug("Migration skipped: {}. Migration lock was refused.", migrationLock.getId());
            return MigrationResult.rejectedResult();
        }
        return MigrationResult.acquiredResult(acquireResult.value());
    }

    private List<String> runMigrations() {
        Timer timer = Timer.start();
        logger.info("Migration started: {}", migrationLock.getId());
        List<String> result = migrationChangeSets.stream()
            .filter(MigrationChangeSet::execute)
            .map(MigrationChangeSet::id)
            .toList();
        logger.info("Migration finished successfully: {} [{}]", migrationLock.getId(), timer.elapsed());
        return result;
    }

    static final class MigrationChangeSet {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        private final String id;
        private final DistributedLock lock;
        private final Runnable action;

        MigrationChangeSet(String id, DistributedLock lock, Runnable action) {
            this.id = id;
            this.lock = lock;
            this.action = action;
        }

        String id() {
            return id;
        }

        boolean execute() {
            Timer timer = Timer.start();
            if (lock.acquire()) {
                logger.debug("Executing migration change set: {}", id);
                try {
                    action.run();
                    logger.info("Migration change set applied: {} [{}]", id, timer.elapsed());
                    return true;
                } catch (Throwable exception) {
                    logger.warn(
                        "Migration change set failure: {} [{}]. Stopping migration process. Fix problem and rerun the migration.",
                        id, timer.elapsed(), exception);
                    lock.release();
                    throw exception;
                }
            }
            logger.info("Migration change set skipped: {}", id);
            return false;
        }
    }
}
