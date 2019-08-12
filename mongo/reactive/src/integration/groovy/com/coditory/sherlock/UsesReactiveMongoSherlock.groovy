package com.coditory.sherlock


import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static MongoInitializer.databaseName
import static MongoInitializer.mongoClient
import static BlockingReactiveSherlockWrapper.blockingReactiveSherlock
import static com.coditory.sherlock.ReactiveMongoSherlockBuilder.reactiveMongoSherlock

trait UsesReactiveMongoSherlock implements DistributedLocksCreator {
  static final String locksCollectionName = "locks"

  @Override
  Sherlock createSherlock(String ownerId, Duration duration, Clock clock) {
    ReactiveSherlock reactiveLocks = reactiveMongoSherlock()
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
