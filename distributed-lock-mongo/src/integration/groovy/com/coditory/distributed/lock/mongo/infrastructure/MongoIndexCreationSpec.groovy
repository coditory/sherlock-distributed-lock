package com.coditory.distributed.lock.mongo.infrastructure

import com.coditory.distributed.lock.DistributedLocks
import com.coditory.distributed.lock.mongo.MongoDistributedLockDriver
import com.coditory.distributed.lock.mongo.UsesMongo
import spock.lang.Specification

import static com.coditory.distributed.lock.tests.base.JsonAssert.assertJsonEqual
import static com.coditory.distributed.lock.tests.base.UpdatableFixedClock.defaultUpdatableFixedClock

class MongoIndexCreationSpec extends Specification implements UsesMongo {
  String otherCollectionName = "otherCollection"
  MongoDistributedLockDriver driver = new MongoDistributedLockDriver(mongoClient, databaseName, otherCollectionName, defaultUpdatableFixedClock())
  DistributedLocks locks = DistributedLocks.builder(driver)
      .build()

  def "should create mongo indexes on first lock"() {
    expect:
      assertJsonEqual(getCollectionIndexes(), "[]")
    when:
      locks.createLock("some-lock")
          .lock()
    then:
      assertJsonEqual(getCollectionIndexes(), """[
        {"v": 2, "key": {"_id": 1}, "name": "_id_", "ns": "distributed-lock-mongo.otherCollection"},
        {"v": 2, "key": {"_id": 1, "acquiredBy": 1, "acquiredAt": 1}, "name": "_id_1_acquiredBy_1_acquiredAt_1", "ns": "distributed-lock-mongo.otherCollection", "background": true}
      ]""")
  }

  private String getCollectionIndexes() {
    List<String> indexes = mongoClient.getDatabase(databaseName)
        .getCollection(otherCollectionName)
        .listIndexes()
        .asList()
        .collect { it.toJson() }
    return "[" + indexes.join(",\n") + "]"
  }
}
