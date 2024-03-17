package com.coditory.sherlock.reactor;

import com.coditory.sherlock.Timer;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.migrator.MigrationResult;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

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
    public Mono<MigrationResult> migrate() {
        return migrationLock
                .acquireAndExecute(runMigrations())
                .defaultIfEmpty(AcquireResult.FAILURE)
                .map(acquireResult -> {
                    if (!acquireResult.isAcquired()) {
                        logger.debug("Migration skipped: {}. Migration lock was refused.", migrationLock.getId());
                    }
                    return new MigrationResult(acquireResult.isAcquired());
                });
    }

    private Mono<AcquireResult> runMigrations() {
        Timer timer = Timer.start();
        logger.info("Migration started: {}", migrationLock.getId());
        Mono<AcquireResult> result = Mono.just(AcquireResult.SUCCESS);
        for (MigrationChangeSet changeSet : migrationChangeSets) {
            result = result.flatMap(r -> changeSet.execute());
        }
        return result.doOnNext((r) -> {
            logger.info("Migration finished successfully: {} [{}]", migrationLock.getId(), timer.elapsed());
        });
    }

    static class MigrationChangeSet {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        private final String id;
        private final DistributedLock lock;
        private final Supplier<Mono<?>> action;

        MigrationChangeSet(String id, DistributedLock lock, Supplier<Mono<?>> action) {
            this.id = id;
            this.lock = lock;
            this.action = action;
        }

        Mono<AcquireResult> execute() {
            Timer timer = Timer.start();
            return lock.acquire()
                    .flatMap((result) -> {
                        if (!result.isAcquired()) {
                            logger.info("Migration change set skipped: {}", id);
                            return Mono.just(result);
                        }
                        logger.debug("Executing migration change set: {}", id);
                        return executeChangeSet(timer);
                    });
        }

        private Mono<AcquireResult> executeChangeSet(Timer timer) {
            return action.get()
                    .flatMap((result) -> {
                        logger.info("Migration change set applied: {} [{}]", id, timer.elapsed());
                        return Mono.just(AcquireResult.SUCCESS);
                    })
                    .onErrorResume((e) -> {
                        logger.warn(
                                "Migration change set failure: {} [{}]. Stopping migration process. Fix problem and rerun the migration.",
                                id, timer.elapsed(), e);
                        return lock.release().flatMap((result) -> Mono.error(e));
                    });
        }
    }
}
