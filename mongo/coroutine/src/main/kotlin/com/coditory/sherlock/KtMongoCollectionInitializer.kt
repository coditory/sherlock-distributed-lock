package com.coditory.sherlock

import com.coditory.sherlock.MongoDistributedLock.INDEX
import com.coditory.sherlock.MongoDistributedLock.INDEX_OPTIONS
import com.mongodb.reactivestreams.client.MongoCollection
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document
import java.util.concurrent.atomic.AtomicBoolean

internal class KtMongoCollectionInitializer(
    private val collection: MongoCollection<Document>
) {
    private val indexesCreated = AtomicBoolean(false)

    suspend fun getInitializedCollection(): MongoCollection<Document> {
        val shouldCreateIndexes = indexesCreated.compareAndSet(false, true)
        if (!shouldCreateIndexes) {
            return collection
        }
        collection.createIndex(INDEX, INDEX_OPTIONS).awaitFirstOrNull()
        return collection
    }
}