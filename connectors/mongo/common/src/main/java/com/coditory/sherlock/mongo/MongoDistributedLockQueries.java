package com.coditory.sherlock.mongo;

import com.coditory.sherlock.mongo.MongoDistributedLock.Fields;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.mongodb.client.model.Filters.*;

public final class MongoDistributedLockQueries {
    private MongoDistributedLockQueries() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    @NotNull
    public static Bson queryReleased(@NotNull String lockId, @NotNull Instant now) {
        expectNonEmpty(lockId, "lockId");
        expectNonNull(now, "now");
        return and(
            eq(Fields.LOCK_ID_FIELD, lockId),
            lte(Fields.EXPIRES_AT_FIELD, now)
        );
    }

    @NotNull
    public static Bson queryAcquired(@NotNull String lockId, @NotNull String ownerId) {
        expectNonEmpty(lockId, "lockId");
        expectNonEmpty(ownerId, "ownerId");
        return and(
            eq(Fields.LOCK_ID_FIELD, lockId),
            eq(Fields.ACQUIRED_BY_FIELD, ownerId)
        );
    }

    @NotNull
    public static Bson queryAcquiredOrReleased(@NotNull String lockId, @NotNull String ownerId, @NotNull Instant now) {
        expectNonEmpty(lockId, "lockId");
        expectNonEmpty(ownerId, "ownerId");
        expectNonNull(now, "now");
        return and(
            eq(Fields.LOCK_ID_FIELD, lockId),
            or(
                eq(Fields.ACQUIRED_BY_FIELD, ownerId),
                lte(Fields.EXPIRES_AT_FIELD, now)
            )
        );
    }

    @NotNull
    public static Bson queryById(@NotNull String lockId) {
        expectNonEmpty(lockId, "lockId");
        return eq(Fields.LOCK_ID_FIELD, lockId);
    }
}
