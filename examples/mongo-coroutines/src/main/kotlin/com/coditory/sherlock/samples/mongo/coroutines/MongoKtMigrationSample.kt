package com.coditory.sherlock.samples.mongo.coroutines

import com.coditory.sherlock.coroutines.migrator.SherlockMigrator
import com.coditory.sherlock.mongo.coroutines.MongoSherlock
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Duration

object MongoKtMigrationSample {
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
            .addChangeSet("change-set-1", Runnable { logger.info("Change-set 1") })
            .addChangeSet("change-set-2", Runnable { logger.info("Change-set 2") })
            .migrate()

        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
            .addChangeSet("change-set-1", Runnable { logger.info("Change-set 1") })
            .addChangeSet("change-set-2", Runnable { logger.info("Change-set 2") })
            .addChangeSet("change-set-3", Runnable { logger.info("Change-set 3") })
            .migrate()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { sample() }
    }
}
