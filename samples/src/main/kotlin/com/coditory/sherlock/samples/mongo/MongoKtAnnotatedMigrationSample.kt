package com.coditory.sherlock.samples.mongo

import com.coditory.sherlock.coroutines.migrator.SherlockMigrator
import com.coditory.sherlock.migrator.ChangeSet
import com.coditory.sherlock.mongo.coroutines.MongoSherlock
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Duration

object MongoKtAnnotatedMigrationSample {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val sherlock =
        MongoSherlock.builder()
            .withClock(Clock.systemUTC())
            .withLockDuration(Duration.ofMinutes(5))
            .withUniqueOwnerId()
            .withLocksCollection(locksCollection())
            .build()

    private fun locksCollection(): MongoCollection<Document> {
        val database = "sherlock"
        val mongoClient = MongoClient.create("mongodb://localhost:27017/$database")
        return mongoClient
            .getDatabase(database)
            .getCollection("locks")
    }

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
