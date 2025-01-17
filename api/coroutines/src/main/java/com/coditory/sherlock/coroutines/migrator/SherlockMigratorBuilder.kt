package com.coditory.sherlock.coroutines.migrator

import com.coditory.sherlock.Preconditions
import com.coditory.sherlock.Preconditions.expectNonEmpty
import com.coditory.sherlock.SherlockDefaults.DEFAULT_MIGRATOR_LOCK_ID
import com.coditory.sherlock.coroutines.DistributedLock
import com.coditory.sherlock.coroutines.Sherlock
import com.coditory.sherlock.coroutines.migrator.SherlockMigrator.MigrationChangeSet
import com.coditory.sherlock.migrator.ChangeSetMethodExtractor
import com.coditory.sherlock.migrator.MigrationChangeSetMethod
import com.coditory.sherlock.migrator.MigrationResult
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

class SherlockMigratorBuilder internal constructor(private val sherlock: Sherlock) {
    private val migrationChangeSets = mutableListOf<MigrationChangeSet>()
    private val migrationLockIds = mutableSetOf<String>()
    private var migrationId = DEFAULT_MIGRATOR_LOCK_ID

    fun setMigrationId(migrationId: String): SherlockMigratorBuilder {
        expectNonEmpty(migrationId, "migrationId")
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

    fun addChangeSet(
        changeSetId: String,
        changeSet: suspend () -> Unit,
    ): SherlockMigratorBuilder {
        ensureUniqueChangeSetId(changeSetId)
        val changeSetLock = createChangeSetLock(changeSetId)
        val migrationChangeSet = MigrationChangeSet(changeSetId, changeSetLock, changeSet)
        migrationChangeSets.add(migrationChangeSet)
        return this
    }

    fun addAnnotatedChangeSets(annotated: Any): SherlockMigratorBuilder {
        Preconditions.expectNonNull(annotated, "object containing change sets")
        ChangeSetMethodExtractor.extractChangeSetMethods(annotated, Void.TYPE)
            .forEach { changeSet ->
                if (ChangeSetMethodExtractor.isCoroutine(changeSet.method())) {
                    addChangeSet(changeSet.id()) { runSuspendedChangeSetMethod(changeSet) }
                } else {
                    addChangeSet(changeSet.id()) { runNonSuspendedChangeSetMethod(changeSet) }
                }
            }
        return this
    }

    private suspend fun runSuspendedChangeSetMethod(changeSet: MigrationChangeSetMethod<Void>) = suspendCoroutineUninterceptedOrReturn<Void> { cont ->
        try {
            changeSet.method.invoke(changeSet.`object`(), cont)
        } catch (e: InvocationTargetException) {
            throw IllegalStateException(
                "Could not invoke suspended changeset method: " + changeSet.method.name,
                e,
            )
        } catch (e: IllegalAccessException) {
            throw IllegalStateException(
                "Could not invoke suspended changeset method: " + changeSet.method.name,
                e,
            )
        }
    }

    private fun runNonSuspendedChangeSetMethod(changeSet: MigrationChangeSetMethod<Void>) {
        try {
            changeSet.method().invoke(changeSet.`object`())
        } catch (e: InvocationTargetException) {
            throw IllegalStateException("Could not invoke changeset method: " + changeSet.method.name, e)
        } catch (e: IllegalAccessException) {
            throw IllegalStateException("Could not invoke changeset method: " + changeSet.method.name, e)
        }
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
}
