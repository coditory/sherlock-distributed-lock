package com.coditory.sherlock.infrastructure

import com.coditory.sherlock.RxMongoHolder
import com.coditory.sherlock.RxSherlock
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import reactor.core.publisher.Flux
import spock.lang.Specification

import static com.coditory.sherlock.RxMongoHolder.databaseName
import static com.coditory.sherlock.RxMongoSherlockBuilder.rxMongoSherlock
import static com.coditory.sherlock.base.JsonAssert.assertJsonEqual

class RxMongoIndexCreationSpec extends Specification {
    String collectionName = "other-locks"
    MongoCollection<Document> collection = RxMongoHolder.getClient()
            .getDatabase(databaseName)
            .getCollection(collectionName)
    RxSherlock locks = rxMongoSherlock()
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
            locks.initialize()
                    .blockingGet()
        then:
            assertIndexesCreated()
    }

    def "should create mongo indexes on first lock"() {
        expect:
            assertNoIndexes()
        when:
            locks.createLock("some-acquire")
                    .acquire().blockingGet()
        then:
            assertIndexesCreated()
    }

    private boolean assertNoIndexes() {
        assertJsonEqual(getCollectionIndexes(), "[]")
        return true
    }

    private boolean assertIndexesCreated() {
        assertJsonEqual(getCollectionIndexes(), """[
        {"v": 2, "key": {"_id": 1, "acquiredBy": 1, "expiresAt": 1}, "name": "_id_1_acquiredBy_1_expiresAt_1", "ns": "$databaseName.$collectionName", "background": true},
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
