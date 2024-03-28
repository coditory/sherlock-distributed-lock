package com.coditory.sherlock.rxjava.migrator;

import com.coditory.sherlock.Timer;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.AcquireResultWithValue;
import com.coditory.sherlock.migrator.MigrationResult;
import com.coditory.sherlock.rxjava.DistributedLock;
import com.coditory.sherlock.rxjava.Sherlock;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

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

    /**
     * Runs the migration process.
     *
     * @return migration result
     */
    @NotNull
    public Single<MigrationResult> migrate() {
        return migrationLock
            .runLocked(runMigrations())
            .map(acquireResult -> {
                if (!acquireResult.acquired()) {
                    logger.debug("Migration skipped: {}. Migration lock was refused.", migrationLock.getId());
                    return MigrationResult.rejectedResult();
                }
                return MigrationResult.acquiredResult(acquireResult.value());
            });
    }

    private Single<List<String>> runMigrations() {
        Timer timer = Timer.start();
        logger.info("Migration started: {}", migrationLock.getId());
        return Flowable.fromIterable(migrationChangeSets)
            .flatMapSingle(MigrationChangeSet::execute)
            .filter(AcquireResultWithValue::acquired)
            .map(AcquireResultWithValue::value)
            .toList()
            .doOnSuccess((r) -> {
                logger.info("Migration finished successfully: {} [{}]", migrationLock.getId(), timer.elapsed());
            });
    }

    static class MigrationChangeSet {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        private final String id;
        private final DistributedLock lock;
        private final Supplier<Single<?>> action;

        MigrationChangeSet(String id, DistributedLock lock, Supplier<Single<?>> action) {
            this.id = id;
            this.lock = lock;
            this.action = action;
        }

        Single<AcquireResultWithValue<String>> execute() {
            Timer timer = Timer.start();
            return lock.acquire()
                .flatMap((result) -> {
                    if (!result.acquired()) {
                        logger.info("Migration change set skipped: {}", id);
                        return Single.just(result);
                    }
                    logger.debug("Executing migration change set: {}", id);
                    return executeChangeSet(timer);
                })
                .map(r -> r.toAcquiredWithValue(id));
        }

        private Single<AcquireResult> executeChangeSet(Timer timer) {
            return action.get()
                .map((result) -> {
                    logger.info("Migration change set applied: {} [{}]", id, timer.elapsed());
                    return AcquireResult.acquiredResult();
                })
                .onErrorResumeNext((e) -> {
                    logger.warn(
                        "Migration change set failure: {} [{}]. Stopping migration process. Fix problem and rerun the migration.",
                        id, timer.elapsed(), e);
                    return lock.release().flatMap((result) -> Single.error(e));
                });
        }
    }
}
