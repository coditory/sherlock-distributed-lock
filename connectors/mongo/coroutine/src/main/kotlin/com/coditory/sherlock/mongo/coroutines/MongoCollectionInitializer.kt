package com.coditory.sherlock.mongo.coroutines

import com.coditory.sherlock.mongo.MongoDistributedLock.INDEX
import com.coditory.sherlock.mongo.MongoDistributedLock.INDEX_OPTIONS
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.Document
import java.util.concurrent.atomic.AtomicBoolean

internal class MongoCollectionInitializer(
    private val collection: MongoCollection<Document>,
) {
    private val indexesCreated = AtomicBoolean(false)

    suspend fun getInitializedCollection(): MongoCollection<Document> {
        val shouldCreateIndexes = indexesCreated.compareAndSet(false, true)
        if (!shouldCreateIndexes) {
            return collection
        }
        collection.createIndex(INDEX, INDEX_OPTIONS)
        return collection
    }
}
