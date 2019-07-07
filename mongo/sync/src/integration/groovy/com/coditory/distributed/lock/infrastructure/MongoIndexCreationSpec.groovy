package com.coditory.distributed.lock.infrastructure

import com.coditory.distributed.lock.DistributedLocks
import com.coditory.distributed.lock.MongoDistributedLocks
import spock.lang.Specification

import static com.coditory.distributed.lock.MongoInitializer.databaseName
import static com.coditory.distributed.lock.MongoInitializer.mongoClient
import static com.coditory.distributed.lock.tests.base.JsonAssert.assertJsonEqual

class MongoIndexCreationSpec extends Specification {
  String otherCollectionName = "otherCollection"
  DistributedLocks locks = MongoDistributedLocks.builder()
      .withMongoClient(mongoClient)
      .withDatabaseName(databaseName)
      .withCollectionName(otherCollectionName)
      .build()

  def "should create mongo indexes on first lock"() {
    expect:
      assertJsonEqual(getCollectionIndexes(), "[]")
    when:
      locks.createLock("some-acquire")
          .acquire()
    then:
      assertJsonEqual(getCollectionIndexes(), """[
        {"v": 2, "key": {"_id": 1, "acquiredBy": 1, "acquiredAt": 1}, "name": "_id_1_acquiredBy_1_acquiredAt_1", "ns": "distributed-acquire-mongo.otherCollection", "background": true},
        {"v": 2, "key": {"_id": 1}, "name": "_id_", "ns": "distributed-acquire-mongo.otherCollection"}
      ]""")
  }

  private String getCollectionIndexes() {
    List<String> indexes = mongoClient.getDatabase(databaseName)
        .getCollection(otherCollectionName)
        .listIndexes()
        .asList()
        .collect { it.toJson() }
        .sort()
    return "[" + indexes.join(",\n") + "]"
  }
}
