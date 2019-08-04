package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static MongoInitializer.databaseName
import static MongoInitializer.mongoClient

trait UsesMongoSherlock implements DistributedLocksCreator {
  static final String locksCollectionName = "locks"

  @Override
  Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
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

