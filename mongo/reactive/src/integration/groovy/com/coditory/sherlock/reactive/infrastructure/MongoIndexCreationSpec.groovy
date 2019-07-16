package com.coditory.sherlock.reactive.infrastructure

import com.coditory.sherlock.reactive.ReactiveMongoSherlock
import com.coditory.sherlock.reactive.ReactiveSherlock
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import reactor.core.publisher.Flux
import spock.lang.Specification

import static com.coditory.sherlock.reactive.MongoInitializer.databaseName
import static com.coditory.sherlock.reactive.MongoInitializer.mongoClient
import static com.coditory.sherlock.tests.base.JsonAssert.assertJsonEqual
import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux

class MongoIndexCreationSpec extends Specification {
  String collectionName = "other-locks"
  MongoCollection<Document> collection = mongoClient.getDatabase(databaseName)
      .getCollection(collectionName)
  ReactiveSherlock locks = ReactiveMongoSherlock.builder()
      .withMongoCollection(collection)
      .build()

  def "should create mongo indexes on first lock"() {
    expect:
      assertJsonEqual(getCollectionIndexes(), "[]")
    when:
      flowPublisherToFlux(locks.createLock("some-acquire").acquire())
          .blockLast()
    and:
      String indexes = getCollectionIndexes()
    then:
      assertJsonEqual(indexes, """[
        {"v": 2, "key": {"_id": 1, "acquiredBy": 1, "acquiredAt": 1}, "name": "_id_1_acquiredBy_1_acquiredAt_1", "ns": "$databaseName.$collectionName", "background": true},
        {"v": 2, "key": {"_id": 1}, "name": "_id_", "ns": "$databaseName.$collectionName"}
      ]""")
  }

  private String getCollectionIndexes() {
    List<String> indexes = Flux.from(collection.listIndexes())
        .collectList()
        .block()
        .collect { it.toJson() }
        .sort()
    return "[" + indexes.join(",\n") + "]"
  }
}
