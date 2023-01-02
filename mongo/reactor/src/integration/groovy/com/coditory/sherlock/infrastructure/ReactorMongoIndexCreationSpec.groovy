package com.coditory.sherlock.infrastructure

import com.coditory.sherlock.ReactorMongoHolder
import com.coditory.sherlock.ReactorSherlock
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import reactor.core.publisher.Flux
import spock.lang.Specification

import static com.coditory.sherlock.ReactorMongoHolder.databaseName
import static com.coditory.sherlock.ReactorMongoSherlockBuilder.reactorMongoSherlock
import static com.coditory.sherlock.base.JsonAssert.assertJsonEqual

class ReactorMongoIndexCreationSpec extends Specification {
    String collectionName = "other-locks"
    MongoCollection<Document> collection = ReactorMongoHolder.getClient()
        .getDatabase(databaseName)
        .getCollection(collectionName)
    ReactorSherlock locks = reactorMongoSherlock()
        .withLocksCollection(collection)
        .build()

    def cleanup() {
        Flux.from(collection.drop())
            .blockLast()
    }

    def "should create mongo indexes on initialize"() {
        expect:
            assertNoIndexes()
        when:
            locks.initialize().block()
        then:
            assertIndexesCreated()
    }

    def "should create mongo indexes on first lock"() {
        expect:
            assertNoIndexes()
        when:
            locks.createLock("some-acquire")
                .acquire().block()
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
}
