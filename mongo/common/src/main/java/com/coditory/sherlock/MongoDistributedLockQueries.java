package com.coditory.sherlock;

import com.coditory.sherlock.MongoDistributedLock.Fields;
import org.bson.conversions.Bson;

import java.time.Instant;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;

final class MongoDistributedLockQueries {
    private MongoDistributedLockQueries() {
        throw new IllegalStateException("Do not instantiate utility class");
    }

    static Bson queryReleased(LockId lockId, Instant now) {
        return and(
                eq(Fields.LOCK_ID_FIELD, lockId.getValue()),
                lte(Fields.EXPIRES_AT_FIELD, now)
        );
    }

    static Bson queryAcquired(LockId lockId, OwnerId ownerId) {
        return and(
                eq(Fields.LOCK_ID_FIELD, lockId.getValue()),
                lte(Fields.ACQUIRED_BY_FIELD, ownerId.getValue())
        );
    }

    static Bson queryAcquiredOrReleased(LockId lockId, OwnerId ownerId, Instant now) {
        return and(
                eq(Fields.LOCK_ID_FIELD, lockId.getValue()),
                or(
                        eq(Fields.ACQUIRED_BY_FIELD, ownerId.getValue()),
                        lte(Fields.EXPIRES_AT_FIELD, now)
                )
        );
    }

    static Bson queryById(LockId lockId) {
        return eq(Fields.LOCK_ID_FIELD, lockId.getValue());
    }
}
