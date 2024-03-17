package com.coditory.sherlock.migrator.base

import com.coditory.sherlock.migrator.MigrationResult
import com.coditory.sherlock.reactor.Sherlock
import com.coditory.sherlock.reactor.SherlockMigrator
import com.coditory.sherlock.reactor.SherlockMigratorBuilder

import static java.util.Objects.requireNonNull

class BlockingReactorMigrator implements BlockingMigrator {
    private final SherlockMigrator migrator

    BlockingReactorMigrator(SherlockMigrator migrator) {
        this.migrator = requireNonNull(migrator)
    }

    @Override
    MigrationResult migrate() {
        return migrator.migrate().block()
    }
}

class BlockingReactorMigratorBuilder implements BlockingMigratorBuilder {
    private final SherlockMigratorBuilder builder

    BlockingReactorMigratorBuilder(Sherlock sherlock) {
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
        return new BlockingReactorMigrator(migrator)
    }

    @Override
    MigrationResult migrate() {
        return build().migrate()
    }
}