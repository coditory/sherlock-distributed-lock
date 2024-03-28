package com.coditory.sherlock.rxjava.migrator

import com.coditory.sherlock.inmem.rxjava.InMemorySherlock
import com.coditory.sherlock.migrator.ChangeSet
import com.coditory.sherlock.migrator.MigrationResult
import com.coditory.sherlock.rxjava.Sherlock
import io.reactivex.rxjava3.core.Single
import spock.lang.Specification

class MigratorRxJavaSpec extends Specification {
    Sherlock sherlock = InMemorySherlock.create()

    def "should execute single"() {
        given:
            RxMigration migration = new RxMigration()
        when:
            MigrationResult result = SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(migration)
                .migrate().blockingGet()
        then:
            result.acquired() == true
            result.executedChangeSets() == ["returning-single"]
            migration.executedMethod
            migration.executedSingle
    }

    def "should not execute single when changeset was executed"() {
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
            migration.executedSingle == false
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
            migration.actions == ["a-method", "a-single", "c-method", "c-single"]
    }

    class RxMigration {
        boolean executedMethod = false
        boolean executedSingle = false

        @ChangeSet(order = 1, id = "returning-single")
        Single<String> returningSingle() {
            executedMethod = true
            return Single.fromCallable {
                executedSingle = true
                return "abc"
            }
        }
    }

    class RxOrderedMigration {
        List<String> actions = new ArrayList<>()

        @ChangeSet(order = 1, id = "a")
        Single<String> changeA() {
            actions.add("a-method")
            return Single.fromCallable {
                actions.add("a-single")
                return "a"
            }
        }

        @ChangeSet(order = 2, id = "b")
        Single<String> changeB() {
            actions.add("b-method")
            return Single.fromCallable {
                actions.add("b-single")
                return "b"
            }
        }

        @ChangeSet(order = 3, id = "c")
        Single<String> changeC() {
            actions.add("c-method")
            return Single.fromCallable {
                actions.add("c-single")
                return "c"
            }
        }
    }
}

