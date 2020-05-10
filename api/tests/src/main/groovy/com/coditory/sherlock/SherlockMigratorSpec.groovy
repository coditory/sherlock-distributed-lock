package com.coditory.sherlock

import com.coditory.sherlock.base.SpecSherlockMigrator
import com.coditory.sherlock.base.SpecSimulatedException
import spock.lang.Specification

import static com.coditory.sherlock.UuidGenerator.uuid
import static com.coditory.sherlock.base.SpecSimulatedException.throwSpecSimulatedException
import static java.util.Objects.requireNonNull

abstract class SherlockMigratorSpec extends Specification {
  String firstChangeSetId = "add index"
  String secondChangeSetId = "remove index"
  String thirdChangeSetId = "add index for the second time"
  List<String> executed = []
  SpecSherlockMigrator migrator

  SherlockMigratorSpec(SpecSherlockMigrator migrator) {
    this.migrator = requireNonNull(migrator)
  }

  def "should run migration with 2 change sets in order"() {
    when:
      migrator
        .addChangeSet(firstChangeSetId, { executed.add(firstChangeSetId) })
        .addChangeSet(secondChangeSetId, { executed.add(secondChangeSetId) })
        .migrate()
    then:
      executed == [firstChangeSetId, secondChangeSetId]
    and:
      assertReleased(migrator.getMigrationId())
      assertAcquired(firstChangeSetId)
      assertAcquired(secondChangeSetId)
  }

  def "should execute only the not applied change set"() {
    given:
      SpecSherlockMigrator migrator = migrator
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
      assertReleased(migrator.getMigrationId())
      assertAcquired(firstChangeSetId)
      assertAcquired(secondChangeSetId)
      assertAcquired(thirdChangeSetId)
  }

  def "should skip migration if it's locked by other process"() {
    given:
      acquire(migrator.getMigrationId())
    when:
      migrator
        .addChangeSet(firstChangeSetId, { executed.add(firstChangeSetId) })
        .migrate()
    then:
      executed == []
  }

  def "should break migration on first change set error"() {
    when:
      migrator
        .addChangeSet(firstChangeSetId, { executed.add(firstChangeSetId) })
        .addChangeSet(secondChangeSetId, { throwSpecSimulatedException() })
        .addChangeSet(thirdChangeSetId, { executed.add(thirdChangeSetId) })
        .migrate()
    then:
      thrown(SpecSimulatedException)
    and:
      executed == [firstChangeSetId]
    and:
      assertReleased(migrator.getMigrationId())
      assertAcquired(firstChangeSetId)
      assertReleased(secondChangeSetId)
      assertReleased(thirdChangeSetId)
  }

  def "should return empty migration result for rejected migration lock"() {
    given:
      acquire(migrator.migrationId)
    when:
      List<String> result = migrator.migrate()
    then:
      result == []
  }

  def "should throw error on duplicated change set id"() {
    when:
      migrator
        .addChangeSet(firstChangeSetId, { throwSpecSimulatedException() })
        .addChangeSet(firstChangeSetId, { throwSpecSimulatedException() })
    then:
      IllegalArgumentException exception = thrown(IllegalArgumentException)
      exception.message.startsWith("Expected unique change set ids")
  }

  def "should throw error on change set id same as migration id"() {
    when:
      migrator
        .addChangeSet(migrator.getMigrationId(), { throwSpecSimulatedException() })
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
