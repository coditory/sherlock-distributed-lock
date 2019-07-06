package com.coditory.distributed.lock.mongo.reactive


import com.coditory.distributed.lock.reactive.ReactiveDistributedLocks
import com.coditory.distributed.lock.reactive.driver.ReactiveDistributedLockDriver
import com.coditory.distributed.lock.tests.base.DistributedLocksCreator
import com.coditory.distributed.lock.tests.base.TestableDistributedLocks
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.BsonDocument
import org.bson.Document
import org.junit.After
import reactor.core.publisher.Flux

import java.time.Clock
import java.time.Duration

import static com.coditory.distributed.lock.mongo.reactive.MongoInitializer.databaseName
import static com.coditory.distributed.lock.mongo.reactive.MongoInitializer.mongoClient
import static com.coditory.distributed.lock.tests.base.TestableDistributedLocksWrapper.testableLocks

trait UsesReactiveMongoDistributedLocks implements DistributedLocksCreator {
  static final String locksCollectionName = "locks"

  @Override
  TestableDistributedLocks createDistributedLocks(String instanceId, Duration duration, Clock clock) {
    ReactiveDistributedLockDriver driver = new ReactiveMongoDistributedLockDriver(mongoClient, databaseName, locksCollectionName, clock)
    ReactiveDistributedLocks reactiveLocks = ReactiveDistributedLocks.builder(driver)
        .withServiceInstanceId(instanceId)
        .withDefaultLockDurationd(duration)
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
