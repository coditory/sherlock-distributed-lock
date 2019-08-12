package com.coditory.sherlock;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.coditory.sherlock.MongoDistributedLock.Fields.ACQUIRED_AT_FIELD;
import static com.coditory.sherlock.MongoDistributedLock.Fields.ACQUIRED_BY_FIELD;
import static com.coditory.sherlock.MongoDistributedLock.Fields.EXPIRES_AT_FIELD;
import static com.coditory.sherlock.MongoDistributedLock.Fields.LOCK_ID_FIELD;
import static com.coditory.sherlock.Preconditions.expectNonNull;

final class MongoDistributedLock {
  interface Fields {
    String LOCK_ID_FIELD = "_id";
    String ACQUIRED_BY_FIELD = "acquiredBy";
    String ACQUIRED_AT_FIELD = "acquiredAt";
    String EXPIRES_AT_FIELD = "expiresAt";
  }

  static final Bson INDEX = Indexes
    .ascending(LOCK_ID_FIELD, ACQUIRED_BY_FIELD, ACQUIRED_AT_FIELD);

  static final IndexOptions INDEX_OPTIONS = new IndexOptions().background(true);

  static MongoDistributedLock fromDocument(Document document) {
    try {
      return new MongoDistributedLock(
        LockId.of(document.getString(LOCK_ID_FIELD)),
        OwnerId.of(document.getString(ACQUIRED_BY_FIELD)),
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

  static MongoDistributedLock fromLockRequest(LockRequest lockRequest, Instant acquiredAt) {
    expectNonNull(lockRequest);
    expectNonNull(acquiredAt);
    Instant releaseAt = Optional.ofNullable(lockRequest.getDuration())
      .map(LockDuration::getValue)
      .map(acquiredAt::plus)
      .map(MongoDistributedLock::truncateToMillis)
      .orElse(null);
    return new MongoDistributedLock(
      lockRequest.getLockId(),
      lockRequest.getOwnerId(),
      truncateToMillis(acquiredAt),
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

  Document toDocument() {
    Document result = new Document()
      .append(LOCK_ID_FIELD, id.getValue())
      .append(ACQUIRED_BY_FIELD, ownerId.getValue())
      .append(ACQUIRED_AT_FIELD, acquiredAt);
    if (expiresAt != null) {
      result = result.append(EXPIRES_AT_FIELD, expiresAt);
    }
    return result;
  }

  boolean hasSameOwner(Document document) {
    if (document == null) {
      return false;
    }
    MongoDistributedLock other = MongoDistributedLock.fromDocument(document);
    return this.ownerId.equals(other.ownerId);
  }

  boolean isActive(Instant now) {
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
