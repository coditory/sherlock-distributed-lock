package com.coditory.distributed.lock.mongo.reactive

import com.coditory.distributed.lock.DistributedLockDriver
import com.coditory.distributed.lock.reactive.driver.ReactiveDistributedLockDriver
import com.coditory.distributed.lock.tests.base.DistributedLockDriverProvider
import com.coditory.distributed.lock.tests.base.UpdatableFixedClock
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.BsonDocument
import org.bson.Document
import org.junit.After
import reactor.core.publisher.Flux

import java.time.Clock

import static com.coditory.distributed.lock.tests.base.BlockingDistributedLockDriver.toBlockingDriver
import static com.coditory.distributed.lock.tests.base.UpdatableFixedClock.defaultUpdatableFixedClock

trait UsesReactiveMongoLockDriver extends UsesReactiveMongo implements DistributedLockDriverProvider {
  static final String locksCollectionName = "locks"
  static final UpdatableFixedClock fixedClock = defaultUpdatableFixedClock()

  @Override
  DistributedLockDriver getDriver(Clock clock) {
    ReactiveDistributedLockDriver reactiveDriver =
        new ReactiveMongoDistributedLockDriver(mongoClient, databaseName, locksCollectionName, fixedClock)
    return toBlockingDriver(reactiveDriver)
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
