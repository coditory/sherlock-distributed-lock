package com.coditory.sherlock.samples.mongo.coroutines

import com.coditory.sherlock.mongo.coroutines.MongoSherlock
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object MongoKtLockSample {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private fun getCollection(): MongoCollection<Document> {
        val database = "sherlock"
        val mongoClient = MongoClient.create("mongodb://localhost:27017/$database")
        return mongoClient
            .getDatabase(database)
            .getCollection<Document>("locks")
    }

    private suspend fun sample() {
        val sherlock = MongoSherlock.create(getCollection())
        val lock = sherlock.createLock("sample-lock")
        lock.runLocked { logger.info("Lock acquired!") }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { sample() }
    }
}
