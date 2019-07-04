package com.coditory.xlock.mongo

import com.mongodb.client.MongoCollection
import org.bson.Document

import static com.mongodb.client.model.Filters.eq

class MongoTestContainerSetupIntgSpec extends MongoIntgSpec {
  def "should start mongo test container"() {
    given:
      MongoCollection<Document> collection = mongoClient
          .getDatabase(locksDatabaseName)
          .getCollection(locksCollectionName)
    and:
      Document someDocument = Document.parse("""{ "_id": 1, "name": "some-name" }""")
      collection.insertOne(someDocument)
    when:
      Document retrieved = collection.find(eq("_id", 1)).first()
    then:
      retrieved == someDocument
  }
}
