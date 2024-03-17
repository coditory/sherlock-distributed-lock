package com.coditory.sherlock;

import com.coditory.sherlock.ReactorMigrator.MigrationChangeSet;
import com.coditory.sherlock.migrator.ChangeSetMethodExtractor;
import com.coditory.sherlock.migrator.MigrationResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

public final class ReactorMigratorBuilder {
    private static final String DEFAULT_MIGRATOR_LOCK_ID = "migrator";
    private final List<MigrationChangeSet> migrationChangeSets = new ArrayList<>();
    private final Set<String> migrationLockIds = new HashSet<>();
    private final ReactorSherlock sherlock;
    private String migrationId = DEFAULT_MIGRATOR_LOCK_ID;

    public ReactorMigratorBuilder(ReactorSherlock sherlock) {
        expectNonNull(sherlock, "sherlock");
        this.sherlock = sherlock;
    }

    @NotNull
    public ReactorMigratorBuilder setMigrationId(String migrationId) {
        expectNonEmpty(migrationId, "migrationId");
        ensureUniqueChangeSetId(migrationId);
        migrationLockIds.add(migrationId);
        this.migrationId = migrationId;
        return this;
    }

    @NotNull
    public ReactorMigratorBuilder addChangeSet(String changeSetId, Runnable changeSet) {
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
    public ReactorMigratorBuilder addChangeSet(String changeSetId, Supplier<Mono<?>> changeSet) {
        expectNonEmpty(changeSetId, "changeSetId");
        expectNonNull(changeSet, "changeSet");
        ensureUniqueChangeSetId(changeSetId);
        migrationLockIds.add(changeSetId);
        ReactorDistributedLock changeSetLock = createChangeSetLock(changeSetId);
        MigrationChangeSet migrationChangeSet = new MigrationChangeSet(changeSetId, changeSetLock, changeSet);
        migrationChangeSets.add(migrationChangeSet);
        return this;
    }

    @NotNull
    public ReactorMigratorBuilder addAnnotatedChangeSets(@NotNull Object object) {
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
    public ReactorMigrator build() {
        ReactorDistributedLock migrationLock = sherlock.createLock()
                .withLockId(migrationId)
                .withPermanentLockDuration()
                .withStaticUniqueOwnerId()
                .build();
        return new ReactorMigrator(migrationLock, migrationChangeSets);
    }

    @NotNull
    public Mono<MigrationResult> migrate() {
        return build().migrate();
    }

    private ReactorDistributedLock createChangeSetLock(String migrationId) {
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
