package com.coditory.distributed.lock.mongo.infrastructure

import com.coditory.distributed.lock.mongo.MongoIntgSpec
import com.mongodb.client.MongoCollection
import org.bson.Document

import static com.mongodb.client.model.Filters.eq

class MongoTestContainerSetupIntgSpec extends MongoIntgSpec {
  def "should start mongo test container"() {
    given:
      MongoCollection<Document> collection = getCollection("test-collection")
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
