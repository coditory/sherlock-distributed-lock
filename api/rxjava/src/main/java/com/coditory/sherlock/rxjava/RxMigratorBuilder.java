package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.migrator.ChangeSetMethodExtractor;
import com.coditory.sherlock.migrator.MigrationResult;
import com.coditory.sherlock.rxjava.RxMigrator.MigrationChangeSet;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

public final class RxMigratorBuilder {
    private static final String DEFAULT_MIGRATOR_LOCK_ID = "migrator";
    private final List<MigrationChangeSet> migrationChangeSets = new ArrayList<>();
    private final Set<String> migrationLockIds = new HashSet<>();
    private final RxSherlock sherlock;
    private String migrationId = DEFAULT_MIGRATOR_LOCK_ID;

    public RxMigratorBuilder(RxSherlock sherlock) {
        expectNonNull(sherlock, "sherlock");
        this.sherlock = sherlock;
    }

    @NotNull
    public RxMigratorBuilder setMigrationId(String migrationId) {
        expectNonEmpty(migrationId, "migrationId");
        ensureUniqueChangeSetId(migrationId);
        migrationLockIds.add(migrationId);
        this.migrationId = migrationId;
        return this;
    }

    @NotNull
    public RxMigratorBuilder addChangeSet(String changeSetId, Runnable changeSet) {
        expectNonEmpty(changeSetId, "changeSetId");
        expectNonNull(changeSet, "changeSet");
        return this.addChangeSet(changeSetId, () -> Single.fromCallable(() -> {
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
    public RxMigratorBuilder addChangeSet(String changeSetId, Supplier<Single<?>> changeSet) {
        expectNonEmpty(changeSetId, "changeSetId");
        expectNonNull(changeSet, "changeSet");
        ensureUniqueChangeSetId(changeSetId);
        migrationLockIds.add(changeSetId);
        RxDistributedLock changeSetLock = createChangeSetLock(changeSetId);
        MigrationChangeSet migrationChangeSet = new MigrationChangeSet(changeSetId, changeSetLock, changeSet);
        migrationChangeSets.add(migrationChangeSet);
        return this;
    }

    @NotNull
    public RxMigratorBuilder addAnnotatedChangeSets(@NotNull Object object) {
        expectNonNull(object, "object containing change sets");
        ChangeSetMethodExtractor.extractChangeSets(object, (action, returnType) -> {
                    if (returnType == void.class) {
                        return () -> Single.fromCallable(() -> {
                            action.get();
                            return true;
                        });
                    }
                    if (Single.class.isAssignableFrom(returnType)) {
                        return () -> Single.fromCallable(action::get);
                    }
                    throw new IllegalArgumentException("Expected method to declare void or Single as return types");
                })
                .forEach(changeSet -> addChangeSet(changeSet.getId(), changeSet::execute));
        return this;
    }

    @NotNull
    public RxMigrator build() {
        RxDistributedLock migrationLock = sherlock.createLock()
                .withLockId(migrationId)
                .withPermanentLockDuration()
                .withStaticUniqueOwnerId()
                .build();
        return new RxMigrator(migrationLock, migrationChangeSets);
    }

    @NotNull
    public Single<MigrationResult> migrate() {
        return build().migrate();
    }

    private RxDistributedLock createChangeSetLock(String migrationId) {
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
