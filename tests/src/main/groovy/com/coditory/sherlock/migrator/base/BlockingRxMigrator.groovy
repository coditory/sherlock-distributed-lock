package com.coditory.sherlock.migrator.base

import com.coditory.sherlock.migrator.MigrationResult
import com.coditory.sherlock.rxjava.Sherlock
import com.coditory.sherlock.rxjava.SherlockMigrator
import com.coditory.sherlock.rxjava.SherlockMigratorBuilder

import static java.util.Objects.requireNonNull

class BlockingRxMigrator implements BlockingMigrator {
    private final SherlockMigrator migrator

    BlockingRxMigrator(SherlockMigrator migrator) {
        this.migrator = requireNonNull(migrator)
    }

    @Override
    MigrationResult migrate() {
        return migrator.migrate().blockingGet()
    }
}

class BlockingRxMigratorBuilder implements BlockingMigratorBuilder {
    private final SherlockMigratorBuilder builder

    BlockingRxMigratorBuilder(Sherlock sherlock) {
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
        return new BlockingRxMigrator(migrator)
    }

    @Override
    MigrationResult migrate() {
        return build().migrate()
    }
}