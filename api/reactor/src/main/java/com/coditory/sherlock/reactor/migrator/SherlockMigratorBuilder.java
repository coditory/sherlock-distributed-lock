package com.coditory.sherlock.reactor.migrator;

import com.coditory.sherlock.migrator.ChangeSetMethodExtractor;
import com.coditory.sherlock.migrator.MigrationResult;
import com.coditory.sherlock.reactor.DistributedLock;
import com.coditory.sherlock.reactor.Sherlock;
import com.coditory.sherlock.reactor.migrator.SherlockMigrator.MigrationChangeSet;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.migrator.MigrationLockId.DEFAULT_MIGRATOR_LOCK_ID;

public final class SherlockMigratorBuilder {
    private final List<MigrationChangeSet> migrationChangeSets = new ArrayList<>();
    private final Set<String> migrationLockIds = new HashSet<>();
    private final Sherlock sherlock;
    private String migrationId = DEFAULT_MIGRATOR_LOCK_ID;

    SherlockMigratorBuilder(@NotNull Sherlock sherlock) {
        expectNonNull(sherlock, "sherlock");
        this.sherlock = sherlock;
    }

    @NotNull
    public SherlockMigratorBuilder setMigrationId(@NotNull String migrationId) {
        expectNonEmpty(migrationId, "migrationId");
        ensureUniqueChangeSetId(migrationId);
        migrationLockIds.add(migrationId);
        this.migrationId = migrationId;
        return this;
    }

    @NotNull
    public SherlockMigratorBuilder addChangeSet(@NotNull String changeSetId, @NotNull Runnable changeSet) {
        expectNonEmpty(changeSetId, "changeSetId");
        expectNonNull(changeSet, "changeSet");
        return this.addChangeSet(changeSetId, () -> Mono.fromCallable(() -> {
            changeSet.run();
            return true;
        }));
    }

    /**
     * Adds change set to migration process.
     *
     * @param changeSetId unique change set id used. This is used as a lock id in migration
     *                    process.
     * @param changeSet   change set action that should be run if change set was not already applied
     * @return the migrator
     */
    @NotNull
    public SherlockMigratorBuilder addChangeSet(@NotNull String changeSetId, @NotNull Supplier<Mono<?>> changeSet) {
        expectNonEmpty(changeSetId, "changeSetId");
        expectNonNull(changeSet, "changeSet");
        ensureUniqueChangeSetId(changeSetId);
        migrationLockIds.add(changeSetId);
        DistributedLock changeSetLock = createChangeSetLock(changeSetId);
        MigrationChangeSet migrationChangeSet = new MigrationChangeSet(changeSetId, changeSetLock, changeSet);
        migrationChangeSets.add(migrationChangeSet);
        return this;
    }

    @NotNull
    public SherlockMigratorBuilder addAnnotatedChangeSets(@NotNull Object object) {
        expectNonNull(object, "object containing change sets");
        ChangeSetMethodExtractor.extractChangeSets(object, (action, returnType) -> {
                    if (returnType == void.class) {
                        return () -> Mono.fromCallable(() -> {
                            action.get();
                            return true;
                        });
                    }
                    if (Mono.class.isAssignableFrom(returnType)) {
                        return () -> {
                            Mono<?> mono = (Mono<?>) action.get();
                            return mono.map(r -> true).defaultIfEmpty(true);
                        };
                    }
                    throw new IllegalArgumentException("Expected method to declare void or Mono as return types");
                })
                .forEach(changeSet -> addChangeSet(changeSet.getId(), changeSet::execute));
        return this;
    }

    @NotNull
    public SherlockMigrator build() {
        DistributedLock migrationLock = sherlock.createLock()
                .withLockId(migrationId)
                .withPermanentLockDuration()
                .withStaticUniqueOwnerId()
                .build();
        return new SherlockMigrator(migrationLock, migrationChangeSets);
    }

    @NotNull
    public Mono<MigrationResult> migrate() {
        return build().migrate();
    }

    private DistributedLock createChangeSetLock(String migrationId) {
        return sherlock.createLock()
                .withLockId(migrationId)
                .withPermanentLockDuration()
                .withStaticUniqueOwnerId()
                .build();
    }

    private void ensureUniqueChangeSetId(String changeSetId) {
        if (migrationLockIds.contains(changeSetId)) {
            throw new IllegalArgumentException(
                    "Expected unique change set ids. Duplicated id: " + changeSetId);
        }
    }
}
