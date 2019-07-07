package com.coditory.sherlock.infrastructure

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.tests.LocksBaseSpec
import com.coditory.sherlock.tests.base.LockTypes
import com.coditory.sherlock.UsesMongoSherlock
import org.bson.BsonDocument
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.sherlock.MongoInitializer.databaseName
import static com.coditory.sherlock.MongoInitializer.mongoClient
import static com.coditory.sherlock.tests.base.JsonAssert.assertJsonEqual

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
        "acquiredBy": "$sampleInstanceId",
        "acquiredAt": { "\$date": ${epochMillis()} },
        "expiresAt": { "\$date": ${epochMillis(defaultLockDuration)} }
      }""")
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
        "acquiredBy": "$sampleInstanceId", 
        "acquiredAt": { "\$date": ${epochMillis()} },
        "expiresAt": { "\$date": ${epochMillis(duration)} }
      }""")
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
        "acquiredBy": "$sampleInstanceId",
        "acquiredAt": { "\$date": ${epochMillis()} },
      }""")
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
    return mongoClient.getDatabase(databaseName)
        .getCollection(locksCollectionName)
        .find(BsonDocument.parse("""{ "_id": "$lockId" }"""))
        .first()
        ?.toJson()
  }

  private long epochMillis(Duration duration = Duration.ZERO) {
    return fixedClock.instant()
        .plus(duration)
        .toEpochMilli()
  }
}
