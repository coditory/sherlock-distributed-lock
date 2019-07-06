package com.coditory.distributed.lock.mongo

import com.coditory.distributed.lock.DistributedLockDriver
import com.coditory.distributed.lock.tests.base.DistributedLockDriverProvider
import com.coditory.distributed.lock.tests.base.UpdatableFixedClock
import com.mongodb.client.MongoCollection
import org.bson.BsonDocument
import org.bson.Document
import org.junit.After

import java.time.Clock

import static com.coditory.distributed.lock.tests.base.UpdatableFixedClock.defaultUpdatableFixedClock

trait UsesMongoLockDriver extends UsesMongo implements DistributedLockDriverProvider {
  static final String locksCollectionName = "locks"
  static final UpdatableFixedClock fixedClock = defaultUpdatableFixedClock()

  @Override
  DistributedLockDriver getDriver(Clock clock) {
    return new MongoDistributedLockDriver(mongoClient, databaseName, locksCollectionName, fixedClock)
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

