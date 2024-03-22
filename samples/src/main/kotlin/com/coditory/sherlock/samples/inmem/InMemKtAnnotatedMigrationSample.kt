package com.coditory.sherlock.samples.inmem

import com.coditory.sherlock.coroutines.migrator.SherlockMigrator
import com.coditory.sherlock.inmem.coroutines.InMemorySherlock
import com.coditory.sherlock.migrator.ChangeSet
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock

object InMemKtAnnotatedMigrationSample {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val sherlock =
        InMemorySherlock.builder()
            .withClock(Clock.systemUTC())
            .withUniqueOwnerId()
            .withSharedStorage()
            .build()

    private suspend fun sample() {
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
            .addAnnotatedChangeSets(AnnotatedMigration())
            .migrate()

        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
            .addAnnotatedChangeSets(AnnotatedMigration2())
            .migrate()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { sample() }
    }

    class AnnotatedMigration {
        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

        @ChangeSet(order = 0, id = "change-set-a")
        fun changeSetA() {
            logger.info("Annotated change-set: A")
        }

        @ChangeSet(order = 1, id = "change-set-b")
        fun changeSetB() {
            logger.info("Annotated change-set: B")
        }
    }

    class AnnotatedMigration2 {
        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

        @ChangeSet(order = 0, id = "change-set-a")
        fun changeSetA() {
            logger.info("Annotated change-set: A")
        }

        @ChangeSet(order = 1, id = "change-set-b")
        fun changeSetB() {
            logger.info("Annotated change-set: B")
        }

        @ChangeSet(order = 2, id = "change-set-c")
        fun changeSetC() {
            logger.info("Annotated change-set: C")
        }
    }
}
