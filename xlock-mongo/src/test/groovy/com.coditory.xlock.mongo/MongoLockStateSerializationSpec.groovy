package com.coditory.xlock.mongo

import org.bson.BsonDocument
import org.bson.Document
import spock.lang.Specification

import java.time.Instant

class MongoLockStateSerializationSpec extends Specification {
  MongoLockState mongoLockState = new MongoLockState(
      "lock-id",
      "acquisition-id",
      "instance-id",
      Instant.parse("2019-07-04T15:39:12.12Z"),
      Instant.parse("2019-07-04T18:39:12.12Z")
  )

  String mongoLockStateAsJson = """
  |{
  |  "_id": "lock-id",
  |  "acquisitionId": "acquisition-id",
  |  "instanceId": "instance-id",
  |  "acquiredAt": {"\$date": 1562254752120},
  |  "releaseAt": {"\$date": 1562265552120}
  |}
  """.trim().stripMargin()

  def "should serialize mongo lock to bson document"() {
    when:
      BsonDocument bsonDocument = mongoLockState.toBsonDocument()
    then:
      bsonDocument == BsonDocument.parse(mongoLockStateAsJson)
  }

  def "should deserialize mongo lock from bson document"() {
    given:
      Document document = Document.parse(mongoLockStateAsJson)
    when:
      MongoLockState deserialized = MongoLockState.fromDocument(document)
    then:
      deserialized == mongoLockState
  }
}
