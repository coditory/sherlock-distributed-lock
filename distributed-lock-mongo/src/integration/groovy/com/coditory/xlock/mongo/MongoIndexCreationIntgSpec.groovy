package com.coditory.xlock.mongo

import com.coditory.xlock.api.DistributedLocks
import com.coditory.xlock.common.LockId

class MongoIndexCreationIntgSpec extends MongoIntgSpec {
  String otherCollectionName = "otherCollection"
  MongoDistributedLockDriver driver = new MongoDistributedLockDriver(mongoClient, locksDatabaseName, otherCollectionName, fixedClock)
  DistributedLocks lockFactory = new DistributedLocks(driver, serviceInstanceId)

  def "should create mongo indexes on first lock"() {
    expect:
      getCollectionIndexCount() == 0
    when:
      lockFactory.createInfiniteLock(LockId.of("some-lock"))
          .lock()
    then:
      getCollectionIndexCount() == 4
  }

  private int getCollectionIndexCount() {
    return mongoClient.getDatabase(locksDatabaseName)
        .getCollection(otherCollectionName)
        .listIndexes()
        .size()
  }
}
