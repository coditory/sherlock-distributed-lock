package com.coditory.sherlock

import reactor.core.publisher.Mono
import spock.lang.Specification

import static com.coditory.sherlock.ReactiveInMemorySherlockBuilder.reactiveInMemorySherlockBuilder
import static com.coditory.sherlock.ReactorSherlockMigrator.ChangeSetResult.migratedChangeSet
import static com.coditory.sherlock.ReactorSherlockMigrator.reactorSherlockMigrator
import static com.coditory.sherlock.UuidGenerator.uuid

class ReactorSherlockMigratorSpec extends Specification {
  String migrationId = "db migration"
  String firstChangeSetId = "add index"
  String secondChangeSetId = "remove index"
  String thirdChangeSetId = "add index for the second time"
  List<String> executed = []
  ReactorSherlock sherlock = reactiveInMemorySherlockBuilder()
    .buildWithApi({ ReactorSherlock.reactorSherlock(it) })

  def "should run migration with 2 change sets in order"() {
    when:
      List<ReactorSherlockMigrator.ChangeSetResult> results = reactorSherlockMigrator(migrationId, sherlock)
        .addChangeSet(firstChangeSetId, changeSet(firstChangeSetId))
        .addChangeSet(secondChangeSetId, changeSet(secondChangeSetId))
        .migrate()
        .collectList()
        .block()
    then:
      executed == [firstChangeSetId, secondChangeSetId]
    and:
      results == [migratedChangeSet(firstChangeSetId), migratedChangeSet(secondChangeSetId)]
    and:
      assertReleased(migrationId)
      assertAcquired(firstChangeSetId, secondChangeSetId)
  }

//  def "should execute only the not applied change set"() {
//    given:
//      ReactorSherlockMigrator migrator = reactorSherlockMigrator(migrationId, sherlock)
//        .addChangeSet(firstChangeSetId, changeSet(firstChangeSetId))
//        .addChangeSet(secondChangeSetId, changeSet(secondChangeSetId))
//    and:
//      List<ReactorSherlockMigrator.ChangeSetResult> results = migrator.migrate().collectList()
//        .block()
//      executed.clear()
//    when:
//      migrator
//        .addChangeSet(thirdChangeSetId, { executed.add(thirdChangeSetId) })
//        .migrate()
//    then:
//      executed == [thirdChangeSetId]
//    and:
//      results == []
//    and:
//      assertReleased(migrationId)
//      assertAcquired(firstChangeSetId)
//      assertAcquired(secondChangeSetId)
//      assertAcquired(thirdChangeSetId)
//  }
//
//  def "should skip migration if it's locked by other process"() {
//    given:
//      acquire(migrationId)
//    when:
//      List<ReactorSherlockMigrator.ChangeSetResult> results = reactorSherlockMigrator(migrationId, sherlock)
//        .addChangeSet(firstChangeSetId, changeSet(firstChangeSetId))
//        .migrate()
//        .collectList()
//        .block()
//    then:
//      executed == []
//    and:
//      results == []
//  }

//  def "should break migration on first change set error"() {
//    when:
//      com.coditory.sherlock.SherlockMigrator.sherlockMigrator(migrationId, sherlock)
//        .addChangeSet(firstChangeSetId, { executed.add(firstChangeSetId) })
//        .addChangeSet(secondChangeSetId, { SpecSimulatedException.throwSpecSimulatedException() })
//        .addChangeSet(thirdChangeSetId, { executed.add(thirdChangeSetId) })
//        .migrate()
//    then:
//      thrown(SpecSimulatedException)
//    and:
//      executed == [firstChangeSetId]
//    and:
//      assertReleased(migrationId)
//      assertAcquired(firstChangeSetId)
//      assertReleased(secondChangeSetId)
//      assertReleased(thirdChangeSetId)
//  }
//
//  def "should return migration result for successful migration"() {
//    given:
//      SherlockMigrator migrator = new SherlockMigrator(migrationId, sherlock)
//    and:
//      int migrationFinishExecutions = 0
//      int migrationRejectedExecutions = 0
//    when:
//      SherlockMigrator.MigrationResult result = migrator.migrate()
//        .onFinish({ migrationFinishExecutions++ })
//        .onRejected({ migrationRejectedExecutions++ })
//    then:
//      result.migrated == true
//      migrationFinishExecutions == 1
//      migrationRejectedExecutions == 0
//  }
//
//  def "should return migration result for rejected migration"() {
//    given:
//      acquire(migrationId)
//      SherlockMigrator migrator = new SherlockMigrator(migrationId, sherlock)
//    and:
//      int migrationFinishExecutions = 0
//      int migrationRejectedExecutions = 0
//    when:
//      SherlockMigrator.MigrationResult result = migrator.migrate()
//        .onFinish({ migrationFinishExecutions++ })
//        .onRejected({ migrationRejectedExecutions++ })
//    then:
//      result.migrated == false
//      migrationFinishExecutions == 0
//      migrationRejectedExecutions == 1
//  }
//
//  def "should throw error on duplicated change set id"() {
//    when:
//      com.coditory.sherlock.SherlockMigrator.sherlockMigrator(migrationId, sherlock)
//        .addChangeSet(firstChangeSetId, { SpecSimulatedException.throwSpecSimulatedException() })
//        .addChangeSet(firstChangeSetId, { SpecSimulatedException.throwSpecSimulatedException() })
//    then:
//      IllegalArgumentException exception = thrown(IllegalArgumentException)
//      exception.message.startsWith("Expected unique change set ids")
//  }
//
//  def "should throw error on change set id same as migration id"() {
//    when:
//      com.coditory.sherlock.SherlockMigrator.sherlockMigrator(migrationId, sherlock)
//        .addChangeSet(migrationId, { SpecSimulatedException.throwSpecSimulatedException() })
//    then:
//      IllegalArgumentException exception = thrown(IllegalArgumentException)
//      exception.message.startsWith("Expected unique change set ids")
//  }

  private Mono<?> changeSet(String id) {
    return Mono.fromCallable({ executed.add(id) })
      .then()
  }

  private void assertAcquired(String... lockIds) {
    lockIds.each {
      assert acquire(it) == false
    }
  }

  private void assertReleased(String... lockIds) {
    lockIds.each {
      assert acquire(it) == true
      sherlock.forceReleaseLock(it).block()
    }
  }

  private boolean acquire(String lockId) {
    return sherlock.createLock()
      .withLockId(lockId)
      .withOwnerId(uuid())
      .build()
      .acquireForever()
      .block()
      .acquired
  }
}
