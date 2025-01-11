package com.coditory.sherlock.mongo

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.base.UpdatableFixedClock
import com.mongodb.client.MongoCollection
import org.bson.Document
import spock.lang.Specification

import java.time.Duration

import static MongoHolder.databaseName
import static com.coditory.sherlock.base.JsonAssert.assertJsonEqual
import static com.coditory.sherlock.base.UpdatableFixedClock.defaultUpdatableFixedClock

abstract class MongoIndexCreationSpec extends Specification implements DistributedLocksCreator {
    static final String collectionName = "other-locks"
    static final UpdatableFixedClock fixedClock = defaultUpdatableFixedClock()
    static final Duration defaultLockDuration = Duration.ofMinutes(10)
    static final String ownerId = "locks_test_instance"
    Sherlock locks

    MongoCollection<Document> collection = MongoHolder.getClient()
        .getDatabase(databaseName)
        .getCollection(collectionName)

    def setup() {
        locks = createSherlock(ownerId, defaultLockDuration, fixedClock, collectionName)
    }

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

    boolean assertNoIndexes() {
        assertJsonEqual(getCollectionIndexes(), "[]")
        return true
    }

    boolean assertIndexesCreated() {
        assertJsonEqual(getCollectionIndexes(), """[
        {"v": 2, "key": {"_id": 1, "acquiredBy": 1, "expiresAt": 1}, "name": "_id_1_acquiredBy_1_expiresAt_1", "background": true},
        {"v": 2, "key": {"_id": 1}, "name": "_id_"}
      ]""")
        return true
    }

    String getCollectionIndexes() {
        List<String> indexes = collection
            .listIndexes()
            .asList()
            .collect { it.toJson() }
            .sort()
        return "[" + indexes.join(",\n") + "]"
    }
}
