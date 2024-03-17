package com.coditory.sherlock.migrator.base

import com.coditory.sherlock.ReactorMigrator
import com.coditory.sherlock.ReactorMigratorBuilder
import com.coditory.sherlock.ReactorSherlock
import com.coditory.sherlock.migrator.MigrationResult

import static java.util.Objects.requireNonNull

class BlockingReactorMigrator implements BlockingMigrator {
    private final ReactorMigrator migrator

    BlockingReactorMigrator(ReactorMigrator migrator) {
        this.migrator = requireNonNull(migrator)
    }

    @Override
    MigrationResult migrate() {
        return migrator.migrate().block()
    }
}

class BlockingReactorMigratorBuilder implements BlockingMigratorBuilder {
    private final ReactorMigratorBuilder builder

    BlockingReactorMigratorBuilder(ReactorSherlock sherlock) {
        requireNonNull(sherlock)
        builder = new ReactorMigratorBuilder(sherlock)
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
        ReactorMigrator migrator = builder.build()
        return new BlockingReactorMigrator(migrator)
    }

    @Override
    MigrationResult migrate() {
        return build().migrate()
    }
}