package com.coditory.distributed.lock.mongo.infrastructure

import com.coditory.distributed.lock.DistributedLocks
import com.coditory.distributed.lock.mongo.MongoDistributedLockDriver
import com.coditory.distributed.lock.mongo.MongoIntgSpec
import com.coditory.distributed.lock.mongo.MongoLocksIntgSpec

import static com.coditory.distributed.lock.mongo.base.JsonAssert.assertJsonEqual

class MongoIndexCreationIntgSpec extends MongoLocksIntgSpec {
  String otherCollectionName = "otherCollection"
  MongoDistributedLockDriver driver = new MongoDistributedLockDriver(MongoIntgSpec.mongoClient, MongoIntgSpec.databaseName, otherCollectionName, MongoLocksIntgSpec.fixedClock)
  DistributedLocks locks = DistributedLocks.builder(driver)
      .withDefaultLockDurationd(MongoLocksIntgSpec.defaultLockDuration)
      .withServiceInstanceId(MongoLocksIntgSpec.sampleInstanceId)
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
    List<String> indexes = MongoIntgSpec.mongoClient.getDatabase(MongoIntgSpec.databaseName)
        .getCollection(otherCollectionName)
        .listIndexes()
        .asList()
        .collect { it.toJson() }
    return "[" + indexes.join(",\n") + "]"
  }
}
