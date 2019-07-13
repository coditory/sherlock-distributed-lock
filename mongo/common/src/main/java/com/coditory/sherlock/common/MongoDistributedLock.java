package com.coditory.sherlock.common;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.coditory.sherlock.common.MongoDistributedLock.Fields.ACQUIRED_AT_FIELD;
import static com.coditory.sherlock.common.MongoDistributedLock.Fields.ACQUIRED_BY_FIELD;
import static com.coditory.sherlock.common.MongoDistributedLock.Fields.EXPIRES_AT_FIELD;
import static com.coditory.sherlock.common.MongoDistributedLock.Fields.LOCK_ID_FIELD;
import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

public final class MongoDistributedLock {
  interface Fields {
    String LOCK_ID_FIELD = "_id";
    String ACQUIRED_BY_FIELD = "acquiredBy";
    String ACQUIRED_AT_FIELD = "acquiredAt";
    String EXPIRES_AT_FIELD = "expiresAt";
  }

  public static final Bson INDEX = Indexes
      .ascending(LOCK_ID_FIELD, ACQUIRED_BY_FIELD, ACQUIRED_AT_FIELD);

  public static final IndexOptions INDEX_OPTIONS = new IndexOptions().background(true);

  public static MongoDistributedLock fromDocument(Document document) {
    return new MongoDistributedLock(
        LockId.of(document.getString(LOCK_ID_FIELD)),
        OwnerId.of(document.getString(ACQUIRED_BY_FIELD)),
        dateToInstant(document.getDate(ACQUIRED_AT_FIELD)),
        dateToInstant(document.getDate(EXPIRES_AT_FIELD))
    );
  }

  private static Instant dateToInstant(Date date) {
    return date != null ? date.toInstant() : null;
  }

  public static MongoDistributedLock fromLockRequest(LockRequest lockRequest, Instant acquiredAt) {
    expectNonNull(lockRequest);
    expectNonNull(acquiredAt);
    Instant releaseAt = Optional.ofNullable(lockRequest.getDuration())
        .map(acquiredAt::plus)
        .orElse(null);
    return new MongoDistributedLock(
        lockRequest.getLockId(),
        lockRequest.getOwnerId(),
        acquiredAt,
        releaseAt
    );
  }

  private final LockId id;
  private final OwnerId ownerId;
  private final Instant acquiredAt;
  private final Instant expiresAt;

  private MongoDistributedLock(
      LockId id,
      OwnerId ownerId,
      Instant createdAt,
      Instant expiresAt) {
    this.id = expectNonNull(id);
    this.ownerId = expectNonNull(ownerId);
    this.acquiredAt = expectNonNull(createdAt);
    this.expiresAt = expiresAt;
  }

  public Document toDocument() {
    Document result = new Document()
        .append(LOCK_ID_FIELD, id.getValue())
        .append(ACQUIRED_BY_FIELD, ownerId.getValue())
        .append(ACQUIRED_AT_FIELD, acquiredAt);
    if (expiresAt != null) {
      result = result.append(EXPIRES_AT_FIELD, expiresAt);
    }
    return result;
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
