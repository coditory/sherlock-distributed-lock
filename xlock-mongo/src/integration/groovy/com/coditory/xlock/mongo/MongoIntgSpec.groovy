package com.coditory.xlock.mongo

import com.coditory.xlock.api.CrossServiceLock
import com.coditory.xlock.api.CrossServiceLockOperations
import com.coditory.xlock.api.LockFactory
import com.coditory.xlock.common.LockId
import com.coditory.xlock.common.ServiceInstanceId
import com.coditory.xlock.mongo.base.UpdatableFixedClock
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import groovy.transform.CompileStatic
import org.bson.BsonDocument
import org.bson.Document
import org.junit.After
import org.testcontainers.containers.GenericContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

import static com.coditory.xlock.mongo.base.UpdatableFixedClock.defaultUpdateableFixedClock

@CompileStatic
@Testcontainers
abstract class MongoIntgSpec extends Specification {
  static final String locksDatabaseName = "mongo-xlock-test"
  static final String locksCollectionName = "locks"
  @Shared
  GenericContainer mongo = new GenericContainer<>("mongo:3.4") // deliberately using an older mongo version
      .withExposedPorts(27017)
  UpdatableFixedClock fixedClock = defaultUpdateableFixedClock()
  ServiceInstanceId serviceInstanceId = ServiceInstanceId.of("mongo-test-instance")
  MongoClient mongoClient = MongoClients.create("mongodb://localhost:${mongo.firstMappedPort}/$locksDatabaseName")
  XLockMongoDriver driver = new XLockMongoDriver(mongoClient, locksDatabaseName, locksCollectionName, fixedClock)
  LockFactory lockFactory = new LockFactory(driver, serviceInstanceId)

  @After
  void clearLockCollection() {
    getLocksCollection().deleteMany(new BsonDocument())
  }

  MongoCollection<Document> getLocksCollection() {
    return mongoClient.getDatabase(locksDatabaseName)
        .getCollection(locksCollectionName)
  }

  CrossServiceLock createLock(String lockId = "sample-lock") {
    return lockFactory.createInfiniteLock(LockId.of(lockId))
  }

  CrossServiceLockOperations createLockOperations(String lockId = "sample-lock") {
    return lockFactory.createLockOperations(LockId.of(lockId))
  }
}
