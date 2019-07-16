package com.coditory.sherlock.reactive


import com.coditory.sherlock.tests.base.DistributedLocksCreator
import com.coditory.sherlock.tests.base.TestableDistributedLocks
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.BsonDocument
import org.bson.Document
import org.junit.After
import reactor.core.publisher.Flux

import java.time.Clock
import java.time.Duration

import static MongoInitializer.databaseName
import static MongoInitializer.mongoClient
import static com.coditory.sherlock.tests.base.BlockingReactiveSherlockWrapper.testableLocks

trait UsesReactiveMongoSherlock implements DistributedLocksCreator {
  static final String locksCollectionName = "locks"

  @Override
  TestableDistributedLocks createDistributedLocks(String instanceId, Duration duration, Clock clock) {
    ReactiveSherlock reactiveLocks = ReactiveMongoSherlock.builder()
        .withLocksCollection(getLocksCollection())
        .withOwnerId(instanceId)
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
