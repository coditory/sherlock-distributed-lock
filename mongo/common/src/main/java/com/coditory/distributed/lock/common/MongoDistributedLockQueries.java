package com.coditory.distributed.lock.common;

import org.bson.conversions.Bson;

import java.time.Instant;

import static com.coditory.distributed.lock.common.MongoDistributedLock.Fields.ACQUIRED_BY_FIELD;
import static com.coditory.distributed.lock.common.MongoDistributedLock.Fields.EXPIRES_AT_FIELD;
import static com.coditory.distributed.lock.common.MongoDistributedLock.Fields.LOCK_ID_FIELD;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;

public final class MongoDistributedLockQueries {
  private MongoDistributedLockQueries() {
    throw new IllegalStateException("Do not instantiate utility class");
  }

  public static Bson queryAcquiredAndReleased(LockId lockId, InstanceId instanceId, Instant now) {
    return and(
        eq(LOCK_ID_FIELD, lockId.getValue()),
        eq(ACQUIRED_BY_FIELD, instanceId.getValue()),
        lte(EXPIRES_AT_FIELD, now)
    );
  }

  public static Bson queryAcquired(LockId lockId, InstanceId instanceId) {
    return and(
        eq(LOCK_ID_FIELD, lockId.getValue()),
        eq(ACQUIRED_BY_FIELD, instanceId.getValue())
    );
  }

  public static Bson queryAcquiredOrReleased(
      LockId lockId, InstanceId instanceId, Instant now) {
    return and(
        eq(LOCK_ID_FIELD, lockId.getValue()),
        or(
            eq(ACQUIRED_BY_FIELD, instanceId.getValue()),
            lte(EXPIRES_AT_FIELD, now)
        )
    );
  }

  public static Bson queryAcquired(LockId lockId) {
    return eq(LOCK_ID_FIELD, lockId.getValue());
  }
}
