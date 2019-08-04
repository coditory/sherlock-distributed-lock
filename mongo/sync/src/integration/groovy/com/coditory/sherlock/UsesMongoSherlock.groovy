package com.coditory.sherlock

import com.coditory.sherlock.tests.base.DistributedLocksCreator
import com.mongodb.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static MongoInitializer.databaseName
import static MongoInitializer.mongoClient

trait UsesMongoSherlock implements DistributedLocksCreator {
  static final String locksCollectionName = "locks"

  @Override
  Sherlock createDistributedLocks(String instanceId, Duration duration, Clock clock) {
    return MongoSherlock.builder()
      .withLocksCollection(getLocksCollection())
      .withOwnerId(instanceId)
      .withLockDuration(duration)
      .withClock(clock)
      .build()
  }

  MongoCollection<Document> getLocksCollection() {
    return mongoClient.getDatabase(databaseName)
      .getCollection(locksCollectionName)
  }
}

