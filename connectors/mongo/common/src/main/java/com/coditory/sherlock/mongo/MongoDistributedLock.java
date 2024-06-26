package com.coditory.sherlock.mongo;

import com.coditory.sherlock.LockRequest;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.mongo.MongoDistributedLock.Fields.*;

public final class MongoDistributedLock {
    interface Fields {
        String LOCK_ID_FIELD = "_id";
        String ACQUIRED_BY_FIELD = "acquiredBy";
        String ACQUIRED_AT_FIELD = "acquiredAt";
        String EXPIRES_AT_FIELD = "expiresAt";
    }

    public static final Bson INDEX = Indexes
        .ascending(LOCK_ID_FIELD, ACQUIRED_BY_FIELD, EXPIRES_AT_FIELD);

    public static final IndexOptions INDEX_OPTIONS = new IndexOptions().background(true);

    @NotNull
    public static MongoDistributedLock fromDocument(@NotNull Document document) {
        expectNonNull(document, "document");
        try {
            return new MongoDistributedLock(
                document.getString(LOCK_ID_FIELD),
                document.getString(ACQUIRED_BY_FIELD),
                dateToInstant(document.getDate(ACQUIRED_AT_FIELD)),
                dateToInstant(document.getDate(EXPIRES_AT_FIELD))
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Could not deserialize lock document", exception);
        }
    }

    private static Instant dateToInstant(Date date) {
        return date != null
            ? truncateToMillis(date.toInstant())
            : null;
    }

    private static Instant truncateToMillis(Instant instant) {
        return instant.truncatedTo(ChronoUnit.MILLIS);
    }

    @NotNull
    public static MongoDistributedLock fromLockRequest(
        @NotNull LockRequest lockRequest,
        @NotNull Instant acquiredAt
    ) {
        expectNonNull(lockRequest, "lockRequest");
        expectNonNull(acquiredAt, "acquiredAt");
        Instant releaseAt = Optional.ofNullable(lockRequest.duration())
            .map(acquiredAt::plus)
            .map(MongoDistributedLock::truncateToMillis)
            .orElse(null);
        return new MongoDistributedLock(
            lockRequest.lockId(),
            lockRequest.ownerId(),
            truncateToMillis(acquiredAt),
            releaseAt
        );
    }

    private final String id;
    private final String ownerId;
    private final Instant acquiredAt;
    private final Instant expiresAt;

    private MongoDistributedLock(
        @NotNull String id,
        @NotNull String ownerId,
        @NotNull Instant createdAt,
        @Nullable Instant expiresAt) {
        this.id = expectNonEmpty(id, "id");
        this.ownerId = expectNonEmpty(ownerId, "ownerId");
        this.acquiredAt = expectNonNull(createdAt, "createdAt");
        this.expiresAt = expiresAt;
    }

    @NotNull
    public Document toDocument() {
        Document result = new Document()
            .append(LOCK_ID_FIELD, id)
            .append(ACQUIRED_BY_FIELD, ownerId)
            .append(ACQUIRED_AT_FIELD, acquiredAt);
        if (expiresAt != null) {
            result = result.append(EXPIRES_AT_FIELD, expiresAt);
        }
        return result;
    }

    public boolean hasSameOwner(@Nullable Document document) {
        if (document == null) {
            return false;
        }
        MongoDistributedLock other = MongoDistributedLock.fromDocument(document);
        return this.ownerId.equals(other.ownerId);
    }

    public boolean isActive(@NotNull Instant now) {
        return expiresAt == null
            || expiresAt.isAfter(now);
    }

    @Override
    public String toString() {
        return "MongoDistributedLock{" +
            "id=" + id +
            ", ownerId=" + ownerId +
            ", acquiredAt=" + acquiredAt +
            ", expiresAt=" + expiresAt +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MongoDistributedLock that = (MongoDistributedLock) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(ownerId, that.ownerId) &&
            Objects.equals(acquiredAt, that.acquiredAt) &&
            Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, acquiredAt, expiresAt);
    }
}
