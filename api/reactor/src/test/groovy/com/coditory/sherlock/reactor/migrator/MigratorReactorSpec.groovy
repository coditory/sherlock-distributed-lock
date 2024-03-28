package com.coditory.sherlock.reactor.migrator

import com.coditory.sherlock.inmem.reactor.InMemorySherlock
import com.coditory.sherlock.migrator.ChangeSet
import com.coditory.sherlock.migrator.MigrationResult
import com.coditory.sherlock.reactor.Sherlock
import reactor.core.publisher.Mono
import spock.lang.Specification

class MigratorReactorSpec extends Specification {
    Sherlock sherlock = InMemorySherlock.create()

    def "should execute mono"() {
        given:
            ReactorMigration migration = new ReactorMigration()
        when:
            MigrationResult result = SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(migration)
                .migrate().block()
        then:
            result.acquired() == true
            result.executedChangeSets() == ["returning-mono"]
            migration.executedMethod
            migration.executedMono
    }

    def "should not execute mono when changeset was executed"() {
        given:
            SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(new ReactorMigration())
                .migrate().block()
        and:
            ReactorMigration migration = new ReactorMigration()
        when:
            MigrationResult result = SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(migration)
                .migrate().block()
        then:
            result.acquired() == true
            result.executedChangeSets() == []
            migration.executedMethod == false
            migration.executedMono == false
    }

    def "should execute changesets in order skipping one"() {
        given:
            sherlock.createLock("b")
                .acquireForever().block()
        and:
            ReactorOrderedMigration migration = new ReactorOrderedMigration()
        when:
            MigrationResult result = SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(migration)
                .migrate().block()
        then:
            result.acquired() == true
            result.executedChangeSets() == ["a", "c"]
            migration.actions == ["a-method", "a-mono", "c-method", "c-mono"]
    }

    class ReactorMigration {
        boolean executedMethod = false
        boolean executedMono = false

        @ChangeSet(order = 1, id = "returning-mono")
        Mono<String> returningMono() {
            executedMethod = true
            return Mono.fromCallable {
                executedMono = true
                return "abc"
            }
        }
    }

    class ReactorOrderedMigration {
        List<String> actions = new ArrayList<>()

        @ChangeSet(order = 1, id = "a")
        Mono<String> changeA() {
            actions.add("a-method")
            return Mono.fromCallable {
                actions.add("a-mono")
                return "a"
            }
        }

        @ChangeSet(order = 2, id = "b")
        Mono<String> changeB() {
            actions.add("b-method")
            return Mono.fromCallable {
                actions.add("b-mono")
                return "b"
            }
        }

        @ChangeSet(order = 3, id = "c")
        Mono<String> changeC() {
            actions.add("c-method")
            return Mono.fromCallable {
                actions.add("c-mono")
                return "c"
            }
        }
    }
}
