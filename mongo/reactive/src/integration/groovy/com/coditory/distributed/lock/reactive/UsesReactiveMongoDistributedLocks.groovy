package com.coditory.distributed.lock.reactive


import com.coditory.distributed.lock.tests.base.DistributedLocksCreator
import com.coditory.distributed.lock.tests.base.TestableDistributedLocks
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.BsonDocument
import org.bson.Document
import org.junit.After
import reactor.core.publisher.Flux

import java.time.Clock
import java.time.Duration

import static MongoInitializer.databaseName
import static MongoInitializer.mongoClient
import static com.coditory.distributed.lock.tests.base.TestableDistributedLocksWrapper.testableLocks

trait UsesReactiveMongoDistributedLocks implements DistributedLocksCreator {
  static final String locksCollectionName = "locks"

  @Override
  TestableDistributedLocks createDistributedLocks(String instanceId, Duration duration, Clock clock) {
    ReactiveDistributedLocks reactiveLocks = ReactiveMongoDistributedLocks.builder()
        .withMongoClient(mongoClient)
        .withDatabaseName(databaseName)
        .withCollectionName(locksCollectionName)
        .withServiceInstanceId(instanceId)
        .withLockDuration(duration)
        .withClock(clock)
        .build()
    return testableLocks(reactiveLocks)
  }

  @After
  void clearLockCollection() {
    Flux.from(getLocksCollection().deleteMany(new BsonDocument()))
        .blockLast()
  }

  MongoCollection<Document> getLocksCollection() {
    return mongoClient.getDatabase(databaseName)
        .getCollection(locksCollectionName)
  }
}
