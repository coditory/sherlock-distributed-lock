package com.coditory.sherlock.reactor


import com.coditory.sherlock.tests.base.DistributedLocksCreator
import com.coditory.sherlock.tests.base.TestableDistributedLocks
import com.coditory.sherlock.reactive.ReactiveMongoSherlock
import com.coditory.sherlock.reactive.ReactiveSherlock
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.BsonDocument
import org.bson.Document
import org.junit.After
import reactor.core.publisher.Flux

import java.time.Clock
import java.time.Duration

import static MongoInitializer.databaseName
import static MongoInitializer.mongoClient
import static ReactorTestableLocksWrapper.testableLocks

trait UsesReactorSherlock implements DistributedLocksCreator {
  static final String locksCollectionName = "sherlock"

  @Override
  TestableDistributedLocks createDistributedLocks(String instanceId, Duration duration, Clock clock) {
    ReactiveSherlock reactiveLocks = ReactiveMongoSherlock.builder()
        .withMongoClient(mongoClient)
        .withDatabaseName(databaseName)
        .withCollectionName(locksCollectionName)
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
