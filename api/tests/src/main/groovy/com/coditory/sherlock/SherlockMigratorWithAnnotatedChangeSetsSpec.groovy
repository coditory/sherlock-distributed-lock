package com.coditory.sherlock

import com.coditory.sherlock.base.SpecSherlockMigrator
import com.coditory.sherlock.base.SpecSherlockMigratorFactory
import spock.lang.Specification
import spock.lang.Unroll

import static com.coditory.sherlock.InMemorySherlockBuilder.inMemorySherlock
import static com.coditory.sherlock.SherlockMigrator.sherlockMigrator
import static com.coditory.sherlock.UuidGenerator.uuid
import static com.coditory.sherlock.base.SpecSimulatedException.throwSpecSimulatedException
import static java.util.Objects.requireNonNull

abstract class SherlockMigratorWithAnnotatedChangeSetsSpec extends Specification {
  static final String migrationId = "db migration"
  static final String firstChangeSetId = "add index"
  static final String secondChangeSetId = "remove index"
  List<String> executed = []
  SpecSher
  SpecSherlockMigratorFactory migratorFactory

  SherlockMigratorWithAnnotatedChangeSetsSpec(SpecSherlockMigratorFactory migratorFactory) {
    this.migratorFactory = requireNonNull(migratorFactory)
  }

  def "should run migration with 2 annotated change sets in order"() {
    given:
      TwoChangeSets twoMigrations = new TwoChangeSets()
    when:
      sherlockMigrator(migrationId, sherlock)
          .addAnnotatedChangeSets(twoMigrations)
          .migrate()
    then:
      executed == [TwoChangeSets.firstChangeSetId, TwoChangeSets.secondChangeSetId]
    and:
      assertReleased(migrationId)
      assertAcquired(TwoChangeSets.firstChangeSetId)
      assertAcquired(TwoChangeSets.secondChangeSetId)
  }

  def "should run migration between manually setup change sets"() {
    given:
      TwoChangeSets twoMigrations = new TwoChangeSets()
    when:
      sherlockMigrator(migrationId, sherlock)
          .addChangeSet(firstChangeSetId, { executed.add(firstChangeSetId) })
          .addAnnotatedChangeSets(twoMigrations)
          .addChangeSet(secondChangeSetId, { executed.add(secondChangeSetId) })
          .migrate()
    then:
      executed == [
          firstChangeSetId,
          TwoChangeSets.firstChangeSetId,
          TwoChangeSets.secondChangeSetId,
          secondChangeSetId
      ]
    and:
      assertReleased(migrationId)
      assertAcquired(firstChangeSetId)
      assertAcquired(TwoChangeSets.firstChangeSetId)
      assertAcquired(TwoChangeSets.secondChangeSetId)
      assertAcquired(secondChangeSetId)
  }

  def "should throw error on duplicated change set ids"() {
    given:
      TwoChangeSets twoMigrations = new TwoChangeSets()
    when:
      sherlockMigrator(migrationId, sherlock)
          .addChangeSet(TwoChangeSets.firstChangeSetId, { throwSpecSimulatedException() })
          .addAnnotatedChangeSets(twoMigrations)
          .migrate()
    then:
      IllegalArgumentException exception = thrown(IllegalArgumentException)
      exception.message.startsWith("Expected unique change set ids")
  }

  @Unroll
  def "should throw error #expectedMessage"() {
    when:
      sherlockMigrator(migrationId, sherlock)
          .addAnnotatedChangeSets(changeSets)
          .migrate()
    then:
      IllegalArgumentException exception = thrown(IllegalArgumentException)
      exception.message.startsWith(expectedMessage)
    where:
      changeSets                          | expectedMessage
      new ChangeSetWithDuplicatedOrders() | "Expected unique change set order values. Duplicated: 1"
      new ChangeSetWithParameters()       | "Expected no declared parameters for method addIndex"
      new ChangeSetWithReturnType()       | "Expected method to declare void as return type. Method:addIndex return type: class java.lang.String"
      new PrivateChangeSet()              | "Expected at least one changeset method annotated with @ChangeSet"
  }

  private SpecSherlockMigrator sherlockMigrator(String migrationId) {
    return migratorFactory.create(migrationId)
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

  class TwoChangeSets {
    static final String firstChangeSetId = "add index - annotated"
    static final String secondChangeSetId = "remove index - annotated"

    @ChangeSet(order = 2, id = secondChangeSetId)
    void removeIndex() {
      executed.add(secondChangeSetId)
    }

    @ChangeSet(order = 1, id = firstChangeSetId)
    void addIndex() {
      executed.add(firstChangeSetId)
    }
  }

  class ChangeSetWithParameters {
    @ChangeSet(order = 1, id = firstChangeSetId)
    void addIndex(String param) {
      executed.add(firstChangeSetId)
    }
  }

  class ChangeSetWithReturnType {
    @ChangeSet(order = 1, id = firstChangeSetId)
    String addIndex() {
      executed.add(firstChangeSetId)
    }
  }

  class PrivateChangeSet {
    @ChangeSet(order = 1, id = firstChangeSetId)
    private void addIndex() {
      executed.add(firstChangeSetId)
    }
  }

  class ChangeSetWithDuplicatedOrders {
    @ChangeSet(order = 1, id = firstChangeSetId)
    void addIndex() {
      executed.add(firstChangeSetId)
    }

    @ChangeSet(order = 1, id = secondChangeSetId)
    void removeIndex() {
      executed.add(secondChangeSetId)
    }
  }
}
