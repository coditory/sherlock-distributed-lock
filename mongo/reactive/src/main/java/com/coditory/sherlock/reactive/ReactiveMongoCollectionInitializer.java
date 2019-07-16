package com.coditory.sherlock.reactive;

import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.sherlock.common.MongoDistributedLock.INDEX;
import static com.coditory.sherlock.common.MongoDistributedLock.INDEX_OPTIONS;

class ReactiveMongoCollectionInitializer {
  private final MongoCollection<Document> collection;
  private final AtomicBoolean indexesCreated = new AtomicBoolean(false);

  ReactiveMongoCollectionInitializer(MongoCollection<Document> collection) {
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
}
