package com.coditory.sherlock.infrastructure

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.LocksBaseSpec
import com.coditory.sherlock.MongoHolder
import com.coditory.sherlock.UsesMongoSherlock
import com.coditory.sherlock.base.LockTypes
import org.bson.BsonDocument
import spock.lang.Unroll

import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter

import static com.coditory.sherlock.base.JsonAssert.assertJsonEqual
import static java.time.temporal.ChronoUnit.MILLIS

class MongoLockStorageSpec extends LocksBaseSpec implements UsesMongoSherlock {
    @Unroll
    def "should preserve lock state for acquired lock - #type"() {
        given:
            DistributedLock lock = createLock(type)
        when:
            lock.acquire()
        then:
            assertJsonEqual(getLockDocument(), """
              {
                "_id": "$sampleLockId",
                "acquiredBy": "$sampleOwnerId",
                "acquiredAt": { "\$date": "${now()}" },
                "expiresAt": { "\$date": "${now(defaultLockDuration)}" }
              }
            """)
        where:
            type << LockTypes.allLockTypes()
    }

    @Unroll
    def "should preserve lock state for acquired lock with custom duration - #type"() {
        given:
            DistributedLock lock = createLock(type)
            Duration duration = Duration.ofDays(1)
        when:
            lock.acquire(duration)
        then:
            assertJsonEqual(getLockDocument(), """
              {
                "_id": "$sampleLockId",
                "acquiredBy": "$sampleOwnerId",
                "acquiredAt": { "\$date": "${now()}" },
                "expiresAt": { "\$date": "${now(duration)}" }
              }
              """)
        where:
            type << LockTypes.allLockTypes()
    }

    @Unroll
    def "should preserve lock state for acquired infinite lock - #type"() {
        given:
            DistributedLock lock = createLock(type)
        when:
            lock.acquireForever()
        then:
            assertJsonEqual(getLockDocument(), """
              {
                "_id": "$sampleLockId",
                "acquiredBy": "$sampleOwnerId",
                "acquiredAt": { "\$date": "${now()}" },
              }
              """)
        where:
            type << LockTypes.allLockTypes()
    }

    @Unroll
    def "should not retrieve state of manually released lock - #type"() {
        given:
            DistributedLock lock = createLock(type)
            lock.acquire()
        when:
            lock.release()
        then:
            getLockDocument() == null
        where:
            type << LockTypes.allLockTypes()
    }

    private String getLockDocument(String lockId = sampleLockId) {
        return MongoHolder.getClient()
                .getDatabase(MongoHolder.databaseName)
                .getCollection(locksCollectionName)
                .find(BsonDocument.parse("""{ "_id": "$lockId" }"""))
                .first()
                ?.toJson()
    }

    private String now(Duration duration = Duration.ZERO) {
        Instant instant = fixedClock.instant()
                .plus(duration)
                .truncatedTo(MILLIS)
        return DateTimeFormatter.ISO_INSTANT.format(instant)
    }
}
