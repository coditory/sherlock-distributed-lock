package com.coditory.distributed.lock


import com.coditory.distributed.lock.tests.base.DistributedLocksCreator
import com.coditory.distributed.lock.tests.base.TestableDistributedLocks
import com.mongodb.client.MongoCollection
import org.bson.BsonDocument
import org.bson.Document
import org.junit.After

import java.time.Clock
import java.time.Duration

import static com.coditory.distributed.lock.MongoInitializer.databaseName
import static com.coditory.distributed.lock.MongoInitializer.mongoClient

trait UsesMongoDistributedLocks implements DistributedLocksCreator {
  static final String locksCollectionName = "locks"

  @Override
  TestableDistributedLocks createDistributedLocks(String instanceId, Duration duration, Clock clock) {
    DistributedLocks locks = MongoDistributedLocks.builder()
        .withMongoClient(mongoClient)
        .withDatabaseName(databaseName)
        .withCollectionName(locksCollectionName)
        .withServiceInstanceId(instanceId)
        .withLockDuration(duration)
        .withClock(clock)
        .build()
    return locks as TestableDistributedLocks
  }

  @After
  void clearLockCollection() {
    getLocksCollection().deleteMany(new BsonDocument())
  }

  MongoCollection<Document> getLocksCollection() {
    return mongoClient.getDatabase(databaseName)
        .getCollection(locksCollectionName)
  }
}

