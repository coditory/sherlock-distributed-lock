package com.coditory.sherlock

import com.coditory.sherlock.KtMigrator.MigrationChangeSet
import com.coditory.sherlock.migrator.ChangeSetMethodExtractor
import com.coditory.sherlock.migrator.MigrationResult

class KtMigratorBuilder(private val sherlock: KtSherlock) {
    private val migrationChangeSets = mutableListOf<MigrationChangeSet>()
    private val migrationLockIds = mutableSetOf<String>()
    private var migrationId = DEFAULT_MIGRATOR_LOCK_ID

    fun setMigrationId(migrationId: String): KtMigratorBuilder {
        Preconditions.expectNonEmpty(migrationId, "migrationId")
        ensureUniqueChangeSetId(migrationId)
        this.migrationId = migrationId
        return this
    }

    fun addChangeSet(
        changeSetId: String,
        changeSet: Runnable,
    ): KtMigratorBuilder {
        ensureUniqueChangeSetId(changeSetId)
        val changeSetLock = createChangeSetLock(changeSetId)
        val action = { changeSet.run() }
        val migrationChangeSet = MigrationChangeSet(changeSetId, changeSetLock, action)
        migrationChangeSets.add(migrationChangeSet)
        return this
    }

    fun addAnnotatedChangeSets(annotated: Any): KtMigratorBuilder {
        Preconditions.expectNonNull(annotated, "object containing change sets")
        ChangeSetMethodExtractor.extractChangeSets(annotated, Void.TYPE)
            .forEach { changeSet -> addChangeSet(changeSet.id) { changeSet.execute() } }
        return this
    }

    fun build(): KtMigrator {
        val migrationLock =
            sherlock.createLock()
                .withLockId(migrationId)
                .withPermanentLockDuration()
                .withStaticUniqueOwnerId()
                .build()
        return KtMigrator(migrationLock, migrationChangeSets)
    }

    suspend fun migrate(): MigrationResult {
        return build().migrate()
    }

    private fun createChangeSetLock(migrationId: String): KtDistributedLock {
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
