package com.coditory.sherlock.mongo.reactor;

import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.mongo.MongoDistributedLock.INDEX;
import static com.coditory.sherlock.mongo.MongoDistributedLock.INDEX_OPTIONS;

class MongoCollectionInitializer {
    private final MongoCollection<Document> collection;
    private final AtomicBoolean indexesCreated = new AtomicBoolean(false);

    MongoCollectionInitializer(MongoCollection<Document> collection) {
        expectNonNull(collection, "collection");
        validateConnection(collection);
        this.collection = collection;
    }

    Mono<MongoCollection<Document>> getInitializedCollection() {
        boolean shouldCreateIndexes = indexesCreated.compareAndSet(false, true);
        if (!shouldCreateIndexes) {
            return Mono.just(collection);
        }
        return Mono.from(collection.createIndex(INDEX, INDEX_OPTIONS))
            .map(result -> collection);
    }

    private void validateConnection(MongoCollection<Document> collection) {
        String readPreference = collection.getReadPreference().getName();
        if (!"primary".equalsIgnoreCase(readPreference)) {
            throw new IllegalArgumentException("Expected Mongo connection with readPreference=primary");
        }
    }
}
