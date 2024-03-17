package com.coditory.sherlock.migrator.base


import com.coditory.sherlock.RxMigrator
import com.coditory.sherlock.RxMigratorBuilder
import com.coditory.sherlock.RxSherlock
import com.coditory.sherlock.migrator.MigrationResult

import static java.util.Objects.requireNonNull

class BlockingRxMigrator implements BlockingMigrator {
    private final RxMigrator migrator

    BlockingRxMigrator(RxMigrator migrator) {
        this.migrator = requireNonNull(migrator)
    }

    @Override
    MigrationResult migrate() {
        return migrator.migrate().blockingGet()
    }
}

class BlockingRxMigratorBuilder implements BlockingMigratorBuilder {
    private final RxMigratorBuilder builder

    BlockingRxMigratorBuilder(RxSherlock sherlock) {
        requireNonNull(sherlock)
        builder = new RxMigratorBuilder(sherlock)
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
        RxMigrator migrator = builder.build()
        return new BlockingRxMigrator(migrator)
    }

    @Override
    MigrationResult migrate() {
        return build().migrate()
    }
}