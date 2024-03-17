package com.coditory.sherlock.coroutines.migrator

import com.coditory.sherlock.Preconditions
import com.coditory.sherlock.coroutines.DistributedLock
import com.coditory.sherlock.coroutines.Sherlock
import com.coditory.sherlock.coroutines.migrator.SherlockMigrator.MigrationChangeSet
import com.coditory.sherlock.migrator.ChangeSetMethodExtractor
import com.coditory.sherlock.migrator.MigrationResult

class SherlockMigratorBuilder(private val sherlock: Sherlock) {
    private val migrationChangeSets = mutableListOf<MigrationChangeSet>()
    private val migrationLockIds = mutableSetOf<String>()
    private var migrationId = DEFAULT_MIGRATOR_LOCK_ID

    fun setMigrationId(migrationId: String): SherlockMigratorBuilder {
        Preconditions.expectNonEmpty(migrationId, "migrationId")
        ensureUniqueChangeSetId(migrationId)
        this.migrationId = migrationId
        return this
    }

    fun addChangeSet(
        changeSetId: String,
        changeSet: Runnable,
    ): SherlockMigratorBuilder {
        ensureUniqueChangeSetId(changeSetId)
        val changeSetLock = createChangeSetLock(changeSetId)
        val action = { changeSet.run() }
        val migrationChangeSet = MigrationChangeSet(changeSetId, changeSetLock, action)
        migrationChangeSets.add(migrationChangeSet)
        return this
    }

    fun addAnnotatedChangeSets(annotated: Any): SherlockMigratorBuilder {
        Preconditions.expectNonNull(annotated, "object containing change sets")
        ChangeSetMethodExtractor.extractChangeSets(annotated, Void.TYPE)
            .forEach { changeSet -> addChangeSet(changeSet.id) { changeSet.execute() } }
        return this
    }

    fun build(): SherlockMigrator {
        val migrationLock =
            sherlock.createLock()
                .withLockId(migrationId)
                .withPermanentLockDuration()
                .withStaticUniqueOwnerId()
                .build()
        return SherlockMigrator(migrationLock, migrationChangeSets)
    }

    suspend fun migrate(): MigrationResult {
        return build().migrate()
    }

    private fun createChangeSetLock(migrationId: String): DistributedLock {
        return sherlock.createLock()
            .withLockId(migrationId)
            .withPermanentLockDuration()
            .withStaticUniqueOwnerId()
            .build()
    }

    private fun ensureUniqueChangeSetId(changeSetId: String) {
        require(!migrationLockIds.contains(changeSetId)) { "Expected unique change set ids. Duplicated id: $changeSetId" }
        migrationLockIds.add(changeSetId)
    }

    companion object {
        private const val DEFAULT_MIGRATOR_LOCK_ID = "migrator"
    }
}
