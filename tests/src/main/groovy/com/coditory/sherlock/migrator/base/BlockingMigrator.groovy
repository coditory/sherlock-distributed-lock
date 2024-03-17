package com.coditory.sherlock.migrator.base

import com.coditory.sherlock.migrator.MigrationResult

interface BlockingMigrator {
    MigrationResult migrate()
}

interface BlockingMigratorBuilder {

    BlockingMigratorBuilder setMigrationId(String migrationId)

    BlockingMigratorBuilder addChangeSet(String changeSetId, Runnable changeSet)

    BlockingMigratorBuilder addAnnotatedChangeSets(Object object)

    BlockingMigrator build()

    MigrationResult migrate()
}