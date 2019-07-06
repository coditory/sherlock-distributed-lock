package com.coditory.distributed.lock.mongo.infrastructure

import com.coditory.distributed.lock.DistributedLock
import com.coditory.distributed.lock.mongo.UsesMongoLockDriver
import com.coditory.distributed.lock.tests.LocksBaseSpec
import com.coditory.distributed.lock.tests.base.LockTypes
import org.bson.BsonDocument
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.distributed.lock.tests.base.JsonAssert.assertJsonEqual

class MongoLockStorageSpec extends LocksBaseSpec implements UsesMongoLockDriver {
  @Unroll
  def "should preserve lock state for acquired lock - #type"() {
    given:
      DistributedLock lock = createLock(type)
    when:
      lock.lock()
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
      lock.lock(duration)
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
      lock.lockInfinitely()
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
      lock.lock()
    when:
      lock.unlock()
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
