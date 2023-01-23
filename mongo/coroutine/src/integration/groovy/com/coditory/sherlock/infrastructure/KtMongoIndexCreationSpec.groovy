package com.coditory.sherlock.infrastructure

import com.coditory.sherlock.KtMongoHolder
import com.coditory.sherlock.Sherlock
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import spock.lang.Specification

import static com.coditory.sherlock.BlockingKtSherlockWrapper.blockingKtSherlock
import static com.coditory.sherlock.KtMongoHolder.databaseName
import static com.coditory.sherlock.KtMongoSherlockBuilder.coroutineMongoSherlock
import static com.coditory.sherlock.PublisherSubscriber.consumeAll
import static com.coditory.sherlock.base.JsonAssert.assertJsonEqual

class KtMongoIndexCreationSpec extends Specification {
    String collectionName = "other-locks"
    MongoCollection<Document> collection = KtMongoHolder.getClient()
            .getDatabase(databaseName)
            .getCollection(collectionName)
    Sherlock locks = blockingKtSherlock(
            coroutineMongoSherlock()
                    .withLocksCollection(collection)
                    .build()
    )

    def cleanup() {
        consumeAll(collection.drop())
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
        {"v": 2, "key": {"_id": 1, "acquiredBy": 1, "expiresAt": 1}, "name": "_id_1_acquiredBy_1_expiresAt_1", "ns": "$databaseName.$collectionName", "background": true},
        {"v": 2, "key": {"_id": 1}, "name": "_id_", "ns": "$databaseName.$collectionName"}
      ]""")
        return true
    }

    private String getCollectionIndexes() {
        List<String> indexes = consumeAll(collection.listIndexes())
                .collect { it.toJson() }
                .sort()
        return "[" + indexes.join(",\n") + "]"
    }
}
