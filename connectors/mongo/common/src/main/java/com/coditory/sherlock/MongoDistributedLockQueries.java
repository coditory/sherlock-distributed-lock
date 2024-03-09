package com.coditory.sherlock;

import com.coditory.sherlock.MongoDistributedLock.Fields;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;

final class MongoDistributedLockQueries {
    private MongoDistributedLockQueries() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    @NotNull
    static Bson queryReleased(@NotNull LockId lockId, @NotNull Instant now) {
        expectNonNull(lockId, "lockId");
        expectNonNull(now, "now");
        return and(
                eq(Fields.LOCK_ID_FIELD, lockId.getValue()),
                lte(Fields.EXPIRES_AT_FIELD, now)
        );
    }

    @NotNull
    static Bson queryAcquired(@NotNull LockId lockId, @NotNull OwnerId ownerId) {
        expectNonNull(lockId, "lockId");
        expectNonNull(ownerId, "ownerId");
        return and(
                eq(Fields.LOCK_ID_FIELD, lockId.getValue()),
                eq(Fields.ACQUIRED_BY_FIELD, ownerId.getValue())
        );
    }

    @NotNull
    static Bson queryAcquiredOrReleased(@NotNull LockId lockId, @NotNull OwnerId ownerId, @NotNull Instant now) {
        expectNonNull(lockId, "lockId");
        expectNonNull(ownerId, "ownerId");
        expectNonNull(now, "now");
        return and(
                eq(Fields.LOCK_ID_FIELD, lockId.getValue()),
                or(
                        eq(Fields.ACQUIRED_BY_FIELD, ownerId.getValue()),
                        lte(Fields.EXPIRES_AT_FIELD, now)
                )
        );
    }

    @NotNull
    static Bson queryById(@NotNull LockId lockId) {
        expectNonNull(lockId, "lockId");
        return eq(Fields.LOCK_ID_FIELD, lockId.getValue());
    }
}
