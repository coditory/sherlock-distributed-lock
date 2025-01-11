package com.coditory.sherlock.samples.mongo.coroutines

import com.coditory.sherlock.coroutines.migrator.SherlockMigrator
import com.coditory.sherlock.migrator.ChangeSet
import com.coditory.sherlock.mongo.coroutines.MongoSherlock
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object MongoKtAnnotatedMigrationSample {
    private fun locksCollection(): MongoCollection<Document> {
        val database = "sherlock"
        val mongoClient = MongoClient.create("mongodb://localhost:27017/$database")
        return mongoClient
            .getDatabase(database)
            .getCollection("locks")
    }

    private suspend fun sample() {
        val sherlock = MongoSherlock.create(locksCollection())
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
        suspend fun changeSetA() {
            logger.info("Annotated change-set: A")
            delay(1)
        }

        @ChangeSet(order = 1, id = "change-set-b")
        suspend fun changeSetB() {
            logger.info("Annotated change-set: B")
            delay(1)
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
