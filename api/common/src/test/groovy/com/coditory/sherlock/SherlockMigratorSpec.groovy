package com.coditory.sherlock

import com.coditory.sherlock.base.SpecSimulatedException
import spock.lang.Specification

import static com.coditory.sherlock.InMemorySherlockBuilder.inMemorySherlock
import static com.coditory.sherlock.SherlockMigrator.sherlockMigrator
import static com.coditory.sherlock.UuidGenerator.uuid
import static com.coditory.sherlock.base.SpecSimulatedException.throwSpecSimulatedException

class SherlockMigratorSpec extends Specification {
  String migrationId = "db migration"
  String firstChangeSetId = "add index"
  String secondChangeSetId = "remove index"
  String thirdChangeSetId = "add index for the second time"
  List<String> executed = []
  Sherlock sherlock = inMemorySherlock()

  def "should run migration with 2 change sets in order"() {
    when:
      sherlockMigrator(migrationId, sherlock)
        .addChangeSet(firstChangeSetId, { executed.add(firstChangeSetId) })
        .addChangeSet(secondChangeSetId, { executed.add(secondChangeSetId) })
        .migrate()
    then:
      executed == [firstChangeSetId, secondChangeSetId]
    and:
      assertReleased(migrationId)
      assertAcquired(firstChangeSetId)
      assertAcquired(secondChangeSetId)
  }

  def "should execute only the not applied change set"() {
    given:
      SherlockMigrator migrator = sherlockMigrator(migrationId, sherlock)
        .addChangeSet(firstChangeSetId, { executed.add(firstChangeSetId) })
        .addChangeSet(secondChangeSetId, { executed.add(secondChangeSetId) })
    and:
      migrator.migrate()
      executed.clear()
    when:
      migrator
        .addChangeSet(thirdChangeSetId, { executed.add(thirdChangeSetId) })
        .migrate()
    then:
      executed == [thirdChangeSetId]
    and:
      assertReleased(migrationId)
      assertAcquired(firstChangeSetId)
      assertAcquired(secondChangeSetId)
      assertAcquired(thirdChangeSetId)
  }

  def "should skip migration if it's locked by other process"() {
    given:
      acquire(migrationId)
    when:
      sherlockMigrator(migrationId, sherlock)
        .addChangeSet(firstChangeSetId, { executed.add(firstChangeSetId) })
        .migrate()
    then:
      executed == []
  }

  def "should break migration on first change set error"() {
    when:
      sherlockMigrator(migrationId, sherlock)
        .addChangeSet(firstChangeSetId, { executed.add(firstChangeSetId) })
        .addChangeSet(secondChangeSetId, { throwSpecSimulatedException() })
        .addChangeSet(thirdChangeSetId, { executed.add(thirdChangeSetId) })
        .migrate()
    then:
      thrown(SpecSimulatedException)
    and:
      executed == [firstChangeSetId]
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
      sherlockMigrator(migrationId, sherlock)
        .addChangeSet(firstChangeSetId, { throwSpecSimulatedException() })
        .addChangeSet(firstChangeSetId, { throwSpecSimulatedException() })
    then:
      IllegalArgumentException exception = thrown(IllegalArgumentException)
      exception.message.startsWith("Expected unique change set ids")
  }

  def "should throw error on change set id same as migration id"() {
    when:
      sherlockMigrator(migrationId, sherlock)
        .addChangeSet(migrationId, { throwSpecSimulatedException() })
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
