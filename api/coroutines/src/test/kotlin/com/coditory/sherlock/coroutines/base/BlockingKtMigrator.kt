package com.coditory.sherlock.coroutines.base

import com.coditory.sherlock.coroutines.Sherlock
import com.coditory.sherlock.coroutines.migrator.SherlockMigrator
import com.coditory.sherlock.coroutines.migrator.SherlockMigratorBuilder
import com.coditory.sherlock.migrator.MigrationResult
import com.coditory.sherlock.migrator.base.BlockingMigrator
import com.coditory.sherlock.migrator.base.BlockingMigratorBuilder
import kotlinx.coroutines.runBlocking

class BlockingKtMigrator(
    private val migrator: SherlockMigrator,
) : BlockingMigrator {
    override fun migrate(): MigrationResult {
        return runBlocking { migrator.migrate() }
    }
}

class BlockingKtMigratorBuilder(sherlock: Sherlock) : BlockingMigratorBuilder {
    private val builder = SherlockMigratorBuilder(sherlock)

    override fun setMigrationId(migrationId: String): BlockingMigratorBuilder {
        builder.setMigrationId(migrationId)
        return this
    }

    override fun addChangeSet(
        changeSetId: String,
        changeSet: Runnable,
    ): BlockingMigratorBuilder {
        builder.addChangeSet(changeSetId, changeSet)
        return this
    }

    override fun addAnnotatedChangeSets(annotated: Any): BlockingMigratorBuilder {
        builder.addAnnotatedChangeSets(annotated)
        return this
    }

    override fun build(): BlockingMigrator {
        val migrator = builder.build()
        return BlockingKtMigrator(migrator)
    }

    override fun migrate(): MigrationResult {
        return build().migrate()
    }
}
