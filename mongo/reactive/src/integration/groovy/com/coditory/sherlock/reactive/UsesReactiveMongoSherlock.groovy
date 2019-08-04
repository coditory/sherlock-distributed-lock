package com.coditory.sherlock.reactive

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.tests.base.DistributedLocksCreator
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static MongoInitializer.databaseName
import static MongoInitializer.mongoClient
import static com.coditory.sherlock.tests.base.BlockingReactiveSherlockWrapper.blockingReactiveSherlock

trait UsesReactiveMongoSherlock implements DistributedLocksCreator {
  static final String locksCollectionName = "locks"

  @Override
  Sherlock createDistributedLocks(String ownerId, Duration duration, Clock clock) {
    ReactiveSherlock reactiveLocks = ReactiveMongoSherlock.builder()
      .withLocksCollection(getLocksCollection())
      .withOwnerId(ownerId)
      .withLockDuration(duration)
      .withClock(clock)
      .build()
    return blockingReactiveSherlock(reactiveLocks)
  }

  MongoCollection<Document> getLocksCollection() {
    return mongoClient.getDatabase(databaseName)
      .getCollection(locksCollectionName)
  }
}
