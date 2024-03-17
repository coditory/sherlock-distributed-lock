package com.coditory.sherlock.migrator

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.migrator.base.BlockingMigrator
import com.coditory.sherlock.migrator.base.BlockingMigratorBuilder

import static java.util.Objects.requireNonNull

class BlockingSyncMigrator implements BlockingMigrator {
    private final SherlockMigrator migrator

    BlockingSyncMigrator(SherlockMigrator migrator) {
        this.migrator = requireNonNull(migrator)
    }

    @Override
    MigrationResult migrate() {
        return migrator.migrate()
    }
}

class BlockingSyncMigratorBuilder implements BlockingMigratorBuilder {
    private final SherlockMigratorBuilder builder

    BlockingSyncMigratorBuilder(Sherlock sherlock) {
        requireNonNull(sherlock)
        builder = new SherlockMigratorBuilder(sherlock)
    }

    @Override
    BlockingMigratorBuilder setMigrationId(String migrationId) {
        builder.setMigrationId(migrationId)
        return this
    }

    @Override
    BlockingMigratorBuilder addChangeSet(String changeSetId, Runnable changeSet) {
        builder.addChangeSet(changeSetId, changeSet)
        return this
    }

    @Override
    BlockingMigratorBuilder addAnnotatedChangeSets(Object object) {
        builder.addAnnotatedChangeSets(object)
        return this
    }

    @Override
    BlockingMigrator build() {
        SherlockMigrator migrator = builder.build()
        return new BlockingSyncMigrator(migrator)
    }

    @Override
    MigrationResult migrate() {
        return build().migrate()
    }
}