package com.coditory.distributed.lock.mongo.infrastructure


import com.mongodb.client.MongoCollection
import org.bson.Document
import spock.lang.Specification

import static com.coditory.distributed.lock.mongo.MongoInitializer.getDatabaseName
import static com.coditory.distributed.lock.mongo.MongoInitializer.getMongoClient
import static com.mongodb.client.model.Filters.eq

class MongoTestContainerSetupSpec extends Specification {
  def "should start mongo test container"() {
    given:
      MongoCollection<Document> collection = mongoClient.getDatabase(databaseName)
          .getCollection("test-collection")
    and:
      Document someDocument = Document.parse("""{ "_id": 1, "name": "some-name" }""")
      collection.insertOne(someDocument)
    when:
      Document retrieved = collection.find(eq("_id", 1)).first()
    then:
      retrieved == someDocument
    cleanup:
      collection.drop()
  }
}
