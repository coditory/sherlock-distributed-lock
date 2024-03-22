package com.coditory.sherlock.samples.mongo

import com.coditory.sherlock.mongo.coroutines.MongoSherlock
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Duration

object MongoKtLockSample {
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
        val lock = sherlock.createLock("sample-lock")
        lock.acquireAndExecute {
            logger.info("Lock acquired!")
        }
    }

    fun main() {
        runBlocking { sample() }
    }
}
