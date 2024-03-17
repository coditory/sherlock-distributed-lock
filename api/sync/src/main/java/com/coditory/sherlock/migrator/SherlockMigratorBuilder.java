package com.coditory.sherlock.migrator;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.migrator.SherlockMigrator.MigrationChangeSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        this.migrationId = migrationId;
        return this;
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
    public SherlockMigratorBuilder addChangeSet(@NotNull String changeSetId, @NotNull Runnable changeSet) {
        expectNonEmpty(changeSetId, "changeSetId");
        expectNonNull(changeSet, "changeSetId");
        ensureUniqueChangeSetId(changeSetId);
        DistributedLock changeSetLock = createChangeSetLock(changeSetId);
        MigrationChangeSet migrationChangeSet = new MigrationChangeSet(changeSetId, changeSetLock, changeSet);
        migrationChangeSets.add(migrationChangeSet);
        return this;
    }

    @NotNull
    public SherlockMigratorBuilder addAnnotatedChangeSets(@NotNull Object object) {
        expectNonNull(object, "object containing change sets");
        ChangeSetMethodExtractor.extractChangeSets(object, void.class)
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
    public MigrationResult migrate() {
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
        migrationLockIds.add(changeSetId);
    }
}
