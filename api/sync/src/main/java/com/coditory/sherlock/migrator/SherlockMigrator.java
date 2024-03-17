package com.coditory.sherlock.migrator;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.DistributedLock.AcquireAndExecuteResult;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.Timer;
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
        AcquireAndExecuteResult acquireResult = migrationLock
                .acquireAndExecute(this::runMigrations)
                .onNotAcquired(() -> logger.debug("Migration skipped: {}. Migration lock was refused.", migrationLock.getId()));
        return new MigrationResult(acquireResult.isAcquired());
    }

    private void runMigrations() {
        Timer timer = Timer.start();
        logger.info("Migration started: {}", migrationLock.getId());
        migrationChangeSets.forEach(MigrationChangeSet::execute);
        logger.info("Migration finished successfully: {} [{}]", migrationLock.getId(), timer.elapsed());
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
}
