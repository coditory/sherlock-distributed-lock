package com.coditory.distributed.lock.mongo.reactive.infrastructure

import com.coditory.distributed.lock.mongo.reactive.ReactiveMongoDistributedLockDriver
import com.coditory.distributed.lock.reactive.ReactiveDistributedLocks
import com.coditory.distributed.lock.reactive.driver.ReactiveDistributedLockDriver
import reactor.core.publisher.Flux
import spock.lang.Specification

import static com.coditory.distributed.lock.mongo.reactive.MongoInitializer.databaseName
import static com.coditory.distributed.lock.mongo.reactive.MongoInitializer.mongoClient
import static com.coditory.distributed.lock.tests.base.JsonAssert.assertJsonEqual
import static com.coditory.distributed.lock.tests.base.UpdatableFixedClock.defaultUpdatableFixedClock
import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux

class MongoIndexCreationSpec extends Specification {
  String otherCollectionName = "otherCollection"
  ReactiveDistributedLockDriver driver = new ReactiveMongoDistributedLockDriver(mongoClient, databaseName, otherCollectionName, defaultUpdatableFixedClock())
  ReactiveDistributedLocks locks = ReactiveDistributedLocks.builder(driver)
      .build()

  def "should create mongo indexes on first lock"() {
    expect:
      assertJsonEqual(getCollectionIndexes(), "[]")
    when:
      flowPublisherToFlux(locks.createLock("some-lock").lock())
          .blockLast()
    then:
      assertJsonEqual(getCollectionIndexes(), """[
        {"v": 2, "key": {"_id": 1, "acquiredBy": 1, "acquiredAt": 1}, "name": "_id_1_acquiredBy_1_acquiredAt_1", "ns": "distributed-lock-mongo.otherCollection", "background": true},
        {"v": 2, "key": {"_id": 1}, "name": "_id_", "ns": "distributed-lock-mongo.otherCollection"}
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
