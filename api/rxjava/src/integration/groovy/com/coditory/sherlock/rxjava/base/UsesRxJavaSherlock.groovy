package com.coditory.sherlock.rxjava.base

import com.coditory.sherlock.reactive.ReactiveMongoSherlock
import com.coditory.sherlock.rxjava.RxJavaSherlock
import com.coditory.sherlock.tests.base.DistributedLocksCreator
import com.coditory.sherlock.tests.base.TestableDistributedLocks
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.BsonDocument
import org.bson.Document
import org.junit.After
import reactor.core.publisher.Flux

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.rxjava.base.BlockingRxJavaSherlock.blockRxJavaSherlock
import static com.coditory.sherlock.rxjava.base.MongoInitializer.databaseName
import static com.coditory.sherlock.rxjava.base.MongoInitializer.mongoClient

trait UsesRxJavaSherlock implements DistributedLocksCreator {
  static final String locksCollectionName = "locks"

  @Override
  TestableDistributedLocks createDistributedLocks(String ownerId, Duration duration, Clock clock) {
    RxJavaSherlock rxJavaLocks = ReactiveMongoSherlock.builder()
        .withMongoCollection(getLocksCollection())
        .withOwnerId(ownerId)
        .withLockDuration(duration)
        .withClock(clock)
        .build { RxJavaSherlock.wrapReactiveSherlock(it) }
    return blockRxJavaSherlock(rxJavaLocks)
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
