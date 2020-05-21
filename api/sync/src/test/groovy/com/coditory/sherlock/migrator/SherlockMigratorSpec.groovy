package com.coditory.sherlock.migrator

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.SherlockMigrator
import com.coditory.sherlock.base.SpecSimulatedException
import spock.lang.Specification

import static com.coditory.sherlock.InMemorySherlockBuilder.inMemorySherlock
import static com.coditory.sherlock.UuidGenerator.uuid
import static com.coditory.sherlock.base.SpecSimulatedException.throwSpecSimulatedException

class SherlockMigratorSpec extends Specification {
    String migrationId = "db migration"
    String firstChangeSetId = "add index"
    String secondChangeSetId = "remove index"
    String thirdChangeSetId = "add index for the second time"
    int firstChangeSetExecutions = 0
    int secondChangeSetExecutions = 0
    int thirdChangeSetExecutions = 0

    Sherlock sherlock = inMemorySherlock()

    def "should run migration with 2 change sets in order"() {
        given:
            SherlockMigrator migrator = new SherlockMigrator(migrationId, sherlock)
                    .addChangeSet(firstChangeSetId, { firstChangeSetExecutions++ })
                    .addChangeSet(secondChangeSetId, { secondChangeSetExecutions++ })
        when:
            migrator.migrate()
        then:
            firstChangeSetExecutions == 1
            secondChangeSetExecutions == 1
        and:
            assertReleased(migrationId)
            assertAcquired(firstChangeSetId)
            assertAcquired(secondChangeSetId)
    }

    def "should run migration only new not applied change sets"() {
        given:
            SherlockMigrator migrator = new SherlockMigrator(migrationId, sherlock)
                    .addChangeSet(firstChangeSetId, { firstChangeSetExecutions++ })
                    .addChangeSet(secondChangeSetId, { secondChangeSetExecutions++ })
        and:
            migrator.migrate()
        when:
            migrator
                    .addChangeSet(thirdChangeSetId, { thirdChangeSetExecutions++ })
                    .migrate()
        then:
            firstChangeSetExecutions == 1
            secondChangeSetExecutions == 1
            thirdChangeSetExecutions == 1
        and:
            assertReleased(migrationId)
            assertAcquired(firstChangeSetId)
            assertAcquired(secondChangeSetId)
            assertAcquired(thirdChangeSetId)
    }

    def "should run skip change set with active locks"() {
        given:
            acquire(secondChangeSetId)
        when:
            new SherlockMigrator(migrationId, sherlock)
                    .addChangeSet(firstChangeSetId, { firstChangeSetExecutions++ })
                    .addChangeSet(secondChangeSetId, { secondChangeSetExecutions++ })
                    .addChangeSet(thirdChangeSetId, { thirdChangeSetExecutions++ })
                    .migrate()
        then:
            firstChangeSetExecutions == 1
            secondChangeSetExecutions == 0
            thirdChangeSetExecutions == 1
    }

    def "should skip migration with active lock"() {
        given:
            acquire(migrationId)
        when:
            new SherlockMigrator(migrationId, sherlock)
                    .addChangeSet(firstChangeSetId, { firstChangeSetExecutions++ })
                    .migrate()
        then:
            firstChangeSetExecutions == 0
    }

    def "should break migration on first change set error"() {
        given:
            SherlockMigrator migrator = new SherlockMigrator(migrationId, sherlock)
                    .addChangeSet(firstChangeSetId, { firstChangeSetExecutions++ })
                    .addChangeSet(secondChangeSetId, { throwSpecSimulatedException() })
                    .addChangeSet(thirdChangeSetId, { thirdChangeSetExecutions++ })
        when:
            migrator.migrate()
        then:
            thrown(SpecSimulatedException)
        and:
            firstChangeSetExecutions == 1
            secondChangeSetExecutions == 0
            thirdChangeSetExecutions == 0
        and:
            assertReleased(migrationId)
            assertAcquired(firstChangeSetId)
            assertReleased(secondChangeSetId)
            assertReleased(thirdChangeSetId)
    }

    def "should return migration result for successful migration"() {
        given:
            SherlockMigrator migrator = new SherlockMigrator(migrationId, sherlock)
        and:
            int migrationFinishExecutions = 0
            int migrationRejectedExecutions = 0
        when:
            SherlockMigrator.MigrationResult result = migrator.migrate()
                    .onFinish({ migrationFinishExecutions++ })
                    .onRejected({ migrationRejectedExecutions++ })
        then:
            result.migrated == true
            migrationFinishExecutions == 1
            migrationRejectedExecutions == 0
    }

    def "should return migration result for rejected migration"() {
        given:
            acquire(migrationId)
            SherlockMigrator migrator = new SherlockMigrator(migrationId, sherlock)
        and:
            int migrationFinishExecutions = 0
            int migrationRejectedExecutions = 0
        when:
            SherlockMigrator.MigrationResult result = migrator.migrate()
                    .onFinish({ migrationFinishExecutions++ })
                    .onRejected({ migrationRejectedExecutions++ })
        then:
            result.migrated == false
            migrationFinishExecutions == 0
            migrationRejectedExecutions == 1
    }

    def "should throw error on duplicated change set id"() {
        when:
            new SherlockMigrator(sherlock)
                    .addChangeSet(firstChangeSetId, { firstChangeSetExecutions++ })
                    .addChangeSet(firstChangeSetId, { secondChangeSetExecutions++ })
        then:
            IllegalArgumentException exception = thrown(IllegalArgumentException)
            exception.message.startsWith("Expected unique change set ids")
    }

    def "should throw error on change set id same as migration id"() {
        when:
            new SherlockMigrator(migrationId, sherlock)
                    .addChangeSet(migrationId, { firstChangeSetExecutions++ })
        then:
            IllegalArgumentException exception = thrown(IllegalArgumentException)
            exception.message.startsWith("Expected unique change set ids")
    }

    private void assertAcquired(String lockId) {
        assert acquire(lockId) == false
    }

    private void assertReleased(String lockId) {
        assert acquire(lockId) == true
        sherlock.forceReleaseLock(lockId)
    }

    private boolean acquire(String lockId) {
        return sherlock.createLock()
                .withLockId(lockId)
                .withOwnerId(uuid())
                .build()
                .acquireForever()
    }
}
