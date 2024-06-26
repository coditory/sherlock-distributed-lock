package com.coditory.sherlock.mongo;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.mongo.MongoDistributedLock.INDEX;
import static com.coditory.sherlock.mongo.MongoDistributedLock.INDEX_OPTIONS;

class MongoCollectionInitializer {
    private final MongoCollection<Document> collection;
    private final AtomicBoolean indexesCreated = new AtomicBoolean(false);

    MongoCollectionInitializer(MongoCollection<Document> collection) {
        expectNonNull(collection, "collection");
        this.collection = collection;
    }

    MongoCollection<Document> getInitializedCollection() {
        boolean shouldCreateIndexes = indexesCreated.compareAndSet(false, true);
        if (!shouldCreateIndexes) {
            return collection;
        }
        collection.createIndex(INDEX, INDEX_OPTIONS);
        return collection;
    }
}
