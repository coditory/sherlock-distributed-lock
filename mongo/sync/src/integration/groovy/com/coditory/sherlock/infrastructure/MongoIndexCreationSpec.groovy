package com.coditory.sherlock.infrastructure

import com.coditory.sherlock.MongoHolder
import com.coditory.sherlock.Sherlock
import com.mongodb.client.MongoCollection
import org.bson.Document
import spock.lang.Specification

import static com.coditory.sherlock.MongoHolder.databaseName
import static com.coditory.sherlock.MongoSherlockBuilder.mongoSherlock
import static com.coditory.sherlock.base.JsonAssert.assertJsonEqual

class MongoIndexCreationSpec extends Specification {
    String collectionName = "other-locks"
    MongoCollection<Document> collection = MongoHolder.getClient()
            .getDatabase(databaseName)
            .getCollection(collectionName)
    Sherlock locks = mongoSherlock()
            .withLocksCollection(collection)
            .build()

    def cleanup() {
        collection.drop()
    }

    def "should create mongo indexes on initialize"() {
        expect:
            assertNoIndexes()
        when:
            locks.initialize()
        then:
            assertIndexesCreated()
    }

    def "should create mongo indexes on first lock"() {
        expect:
            assertNoIndexes()
        when:
            locks.createLock("some-acquire")
                    .acquire()
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
        List<String> indexes = collection
                .listIndexes()
                .asList()
                .collect { it.toJson() }
                .sort()
        return "[" + indexes.join(",\n") + "]"
    }
}
