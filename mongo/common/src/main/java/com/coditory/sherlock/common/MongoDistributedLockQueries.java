package com.coditory.sherlock.common;

import com.coditory.sherlock.common.MongoDistributedLock.Fields;
import org.bson.conversions.Bson;

import java.time.Instant;

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
        eq(Fields.LOCK_ID_FIELD, lockId.getValue()),
        eq(Fields.ACQUIRED_BY_FIELD, instanceId.getValue()),
        lte(Fields.EXPIRES_AT_FIELD, now)
    );
  }

  public static Bson queryAcquired(LockId lockId, InstanceId instanceId) {
    return and(
        eq(Fields.LOCK_ID_FIELD, lockId.getValue()),
        eq(Fields.ACQUIRED_BY_FIELD, instanceId.getValue())
    );
  }

  public static Bson queryAcquiredOrReleased(
      LockId lockId, InstanceId instanceId, Instant now) {
    return and(
        eq(Fields.LOCK_ID_FIELD, lockId.getValue()),
        or(
            eq(Fields.ACQUIRED_BY_FIELD, instanceId.getValue()),
            lte(Fields.EXPIRES_AT_FIELD, now)
        )
    );
  }

  public static Bson queryAcquired(LockId lockId) {
    return eq(Fields.LOCK_ID_FIELD, lockId.getValue());
  }
}
