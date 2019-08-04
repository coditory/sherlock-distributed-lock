package com.coditory.sherlock.infrastructure


import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import org.junit.After
import reactor.core.publisher.Flux
import spock.lang.Specification

import java.util.concurrent.Flow.Publisher

import static com.coditory.sherlock.MongoInitializer.databaseName
import static com.coditory.sherlock.MongoInitializer.mongoClient
import static com.coditory.sherlock.base.JsonAssert.assertJsonEqual
import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux

class MongoIndexCreationSpec extends Specification {
  String collectionName = "other-locks"
  MongoCollection<Document> collection = mongoClient.getDatabase(databaseName)
      .getCollection(collectionName)
  com.coditory.sherlock.ReactiveSherlock locks = com.coditory.sherlock.ReactiveMongoSherlock.builder()
      .withLocksCollection(collection)
      .build()

  @After
  def removeCollection() {
    Flux.from(collection.drop())
        .blockLast()
  }

  def "should create mongo indexes on initialize"() {
    expect:
      assertNoIndexes()
    when:
      block(locks.initialize())
    then:
      assertIndexesCreated()
  }

  def "should create mongo indexes on first lock"() {
    expect:
      assertNoIndexes()
    when:
      block(locks.createLock("some-acquire").acquire())
    then:
      assertIndexesCreated()
  }

  private boolean assertNoIndexes() {
    assertJsonEqual(getCollectionIndexes(), "[]")
    return true
  }

  private boolean assertIndexesCreated() {
    assertJsonEqual(getCollectionIndexes(), """[
        {"v": 2, "key": {"_id": 1, "acquiredBy": 1, "acquiredAt": 1}, "name": "_id_1_acquiredBy_1_acquiredAt_1", "ns": "$databaseName.$collectionName", "background": true},
        {"v": 2, "key": {"_id": 1}, "name": "_id_", "ns": "$databaseName.$collectionName"}
      ]""")
    return true
  }

  private String getCollectionIndexes() {
    List<String> indexes = Flux.from(collection.listIndexes())
        .collectList()
        .block()
        .collect { it.toJson() }
        .sort()
    return "[" + indexes.join(",\n") + "]"
  }

  private <T> T block(Publisher<T> publisher) {
    return flowPublisherToFlux(publisher).single().block()
  }
}
