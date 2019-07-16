package com.coditory.sherlock.infrastructure

import com.coditory.sherlock.MongoSherlock
import com.coditory.sherlock.Sherlock
import com.mongodb.client.MongoCollection
import org.bson.Document
import spock.lang.Specification

import static com.coditory.sherlock.MongoInitializer.databaseName
import static com.coditory.sherlock.MongoInitializer.mongoClient
import static com.coditory.sherlock.tests.base.JsonAssert.assertJsonEqual

class MongoIndexCreationSpec extends Specification {
  String collectionName = "other-locks"
  MongoCollection<Document> collection = mongoClient.getDatabase(databaseName)
      .getCollection(collectionName)
  Sherlock locks = MongoSherlock.builder()
      .withMongoCollection(collection)
      .build()

  def "should create mongo indexes on first lock"() {
    expect:
      assertJsonEqual(getCollectionIndexes(), "[]")
    when:
      locks.createLock("some-acquire")
          .acquire()
    then:
      assertJsonEqual(getCollectionIndexes(), """[
        {"v": 2, "key": {"_id": 1, "acquiredBy": 1, "acquiredAt": 1}, "name": "_id_1_acquiredBy_1_acquiredAt_1", "ns": "$databaseName.$collectionName", "background": true},
        {"v": 2, "key": {"_id": 1}, "name": "_id_", "ns": "$databaseName.$collectionName"}
      ]""")
  }

  private String getCollectionIndexes() {
    List<String> indexes = collection
        .listIndexes()
        .asList()
        .collect { it.toJson() }
        .sort()
    return "[" + indexes.join(",\n") + "]"
  }
}
