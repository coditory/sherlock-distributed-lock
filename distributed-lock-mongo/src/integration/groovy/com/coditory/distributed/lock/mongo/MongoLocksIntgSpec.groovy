package com.coditory.distributed.lock.mongo

import com.coditory.distributed.lock.DistributedLock
import com.coditory.distributed.lock.DistributedLocks
import com.coditory.distributed.lock.mongo.base.LockTypes
import com.coditory.distributed.lock.mongo.base.UpdatableFixedClock
import com.mongodb.client.MongoCollection
import groovy.transform.CompileStatic
import org.bson.BsonDocument
import org.bson.Document
import org.junit.After

import java.time.Duration

import static com.coditory.distributed.lock.mongo.base.UpdatableFixedClock.defaultUpdatableFixedClock

@CompileStatic
abstract class MongoLocksIntgSpec extends MongoIntgSpec {
  static final String locksCollectionName = "locks"
  static final UpdatableFixedClock fixedClock = defaultUpdatableFixedClock()
  static final Duration defaultLockDuration = Duration.ofMinutes(10)
  static final String sampleInstanceId = "mongo-test-instance"
  static final MongoDistributedLockDriver driver = new MongoDistributedLockDriver(mongoClient, databaseName, locksCollectionName, fixedClock)
  static final String sampleLockId = "sample-lock-id"

  @After
  void clearLockCollection() {
    getLocksCollection().deleteMany(new BsonDocument())
  }

  @After
  void resetClock() {
    fixedClock.reset()
  }

  MongoCollection<Document> getLocksCollection() {
    return mongoClient.getDatabase(databaseName)
        .getCollection(locksCollectionName)
  }

  static DistributedLock createLock(
      LockTypes type,
      String lockId = sampleLockId,
      String instanceId = sampleInstanceId,
      Duration duration = defaultLockDuration) {
    return type.createLock(
        driver,
        lockId,
        instanceId,
        duration
    )
  }

  static DistributedLock reentrantLock(String lockId = sampleLockId, String instanceId = sampleInstanceId, Duration duration = defaultLockDuration) {
    return distributedLocks(instanceId, duration)
        .createReentrantLock(lockId)
  }

  static DistributedLocks distributedLocks(String instanceId = sampleInstanceId, Duration duration = defaultLockDuration) {
    return DistributedLocks.builder(driver)
        .withDefaultLockDurationd(duration)
        .withServiceInstanceId(instanceId)
        .build()
  }
}

