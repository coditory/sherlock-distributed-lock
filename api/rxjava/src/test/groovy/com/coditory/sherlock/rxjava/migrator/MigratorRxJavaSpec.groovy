package com.coditory.sherlock.rxjava.migrator

import com.coditory.sherlock.inmem.rxjava.InMemorySherlock
import com.coditory.sherlock.migrator.ChangeSet
import com.coditory.sherlock.migrator.MigrationResult
import com.coditory.sherlock.rxjava.Sherlock
import io.reactivex.rxjava3.core.Completable
import spock.lang.Specification

class MigratorRxJavaSpec extends Specification {
    Sherlock sherlock = InMemorySherlock.create()

    def "should execute completable"() {
        given:
            RxMigration migration = new RxMigration()
        when:
            MigrationResult result = SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(migration)
                .migrate().blockingGet()
        then:
            result.acquired() == true
            result.executedChangeSets() == ["returning-completable"]
            migration.executedMethod
            migration.executedCompletable
    }

    def "should not execute completable when changeset was executed"() {
        given:
            SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(new RxMigration())
                .migrate().blockingGet()
        and:
            RxMigration migration = new RxMigration()
        when:
            MigrationResult result = SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(migration)
                .migrate().blockingGet()
        then:
            result.acquired() == true
            result.executedChangeSets() == []
            migration.executedMethod == false
            migration.executedCompletable == false
    }

    def "should execute changesets in order skipping one"() {
        given:
            sherlock.createLock("b")
                .acquireForever().blockingGet()
        and:
            RxOrderedMigration migration = new RxOrderedMigration()
        when:
            MigrationResult result = SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(migration)
                .migrate().blockingGet()
        then:
            result.acquired() == true
            result.executedChangeSets() == ["a", "c"]
            migration.actions == ["a-method", "a-completable", "c-method", "c-completable"]
    }

    class RxMigration {
        boolean executedMethod = false
        boolean executedCompletable = false

        @ChangeSet(order = 1, id = "returning-completable")
        Completable returningCompletable() {
            executedMethod = true
            return Completable.fromRunnable {
                executedCompletable = true
            }
        }
    }

    class RxOrderedMigration {
        List<String> actions = new ArrayList<>()

        @ChangeSet(order = 1, id = "a")
        Completable changeA() {
            actions.add("a-method")
            return Completable.fromRunnable {
                actions.add("a-completable")
            }
        }

        @ChangeSet(order = 2, id = "b")
        Completable changeB() {
            actions.add("b-method")
            return Completable.fromRunnable {
                actions.add("b-completable")
            }
        }

        @ChangeSet(order = 3, id = "c")
        Completable changeC() {
            actions.add("c-method")
            return Completable.fromRunnable {
                actions.add("c-completable")
            }
        }
    }
}

