package com.coditory.distributed.lock.reactive.infrastructure

import com.coditory.distributed.lock.reactive.ReactiveDistributedLocks
import com.coditory.distributed.lock.reactive.ReactiveMongoDistributedLocks
import reactor.core.publisher.Flux
import spock.lang.Specification

import static com.coditory.distributed.lock.reactive.MongoInitializer.databaseName
import static com.coditory.distributed.lock.reactive.MongoInitializer.mongoClient
import static com.coditory.distributed.lock.tests.base.JsonAssert.assertJsonEqual
import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux

class MongoIndexCreationSpec extends Specification {
  String otherCollectionName = "otherCollection"
  ReactiveDistributedLocks locks = ReactiveMongoDistributedLocks.builder()
      .withMongoClient(mongoClient)
      .withDatabaseName(databaseName)
      .withCollectionName(otherCollectionName)
      .build()

  def "should create mongo indexes on first lock"() {
    expect:
      assertJsonEqual(getCollectionIndexes(), "[]")
    when:
      flowPublisherToFlux(locks.createLock("some-acquire").acquire())
          .blockLast()
    then:
      assertJsonEqual(getCollectionIndexes(), """[
        {"v": 2, "key": {"_id": 1, "acquiredBy": 1, "acquiredAt": 1}, "name": "_id_1_acquiredBy_1_acquiredAt_1", "ns": "distributed-acquire-mongo.otherCollection", "background": true},
        {"v": 2, "key": {"_id": 1}, "name": "_id_", "ns": "distributed-acquire-mongo.otherCollection"}
      ]""")
  }

  private String getCollectionIndexes() {
    List<String> indexes = Flux.from(mongoClient.getDatabase(databaseName)
        .getCollection(otherCollectionName)
        .listIndexes())
        .collectList()
        .block()
        .collect { it.toJson() }
        .sort()
    return "[" + indexes.join(",\n") + "]"
  }
}
