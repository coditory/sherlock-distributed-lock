package com.coditory.sherlock.mongo.coroutines

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.Document

// Used for tests, as some kotlin specific constructs don't work with groovy.
internal object MongoOperations {
    @JvmStatic
    fun getLocksCollection(
        mongoClient: MongoClient,
        databaseName: String,
        collectionName: String,
    ): MongoCollection<Document> {
        return mongoClient
            .getDatabase(databaseName)
            .getCollection(collectionName)
    }
}
