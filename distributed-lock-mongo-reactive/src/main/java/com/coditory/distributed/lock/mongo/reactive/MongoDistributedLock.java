package com.coditory.distributed.lock.mongo.reactive;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;
import com.coditory.distributed.lock.common.LockRequest;
import org.bson.Document;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.ACQUIRED_AT_FIELD;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.ACQUIRED_BY_FIELD;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.EXPIRES_AT_FIELD;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.LOCK_ID_FIELD;

final class MongoDistributedLock {
  interface Fields {
    String LOCK_ID_FIELD = "_id";
    String ACQUIRED_BY_FIELD = "acquiredBy";
    String ACQUIRED_AT_FIELD = "acquiredAt";
    String EXPIRES_AT_FIELD = "expiresAt";
  }

  static MongoDistributedLock fromDocument(Document document) {
    return new MongoDistributedLock(
        LockId.of(document.getString(LOCK_ID_FIELD)),
        InstanceId.of(document.getString(ACQUIRED_BY_FIELD)),
        dateToInstant(document.getDate(ACQUIRED_AT_FIELD)),
        dateToInstant(document.getDate(EXPIRES_AT_FIELD))
    );
  }

  private static Instant dateToInstant(Date date) {
    return date != null ? date.toInstant() : null;
  }

  static MongoDistributedLock fromLockRequest(LockRequest lockRequest, Instant acquiredAt) {
    expectNonNull(lockRequest);
    expectNonNull(acquiredAt);
    Instant releaseAt = Optional.ofNullable(lockRequest.getDuration())
        .map(acquiredAt::plus)
        .orElse(null);
    return new MongoDistributedLock(
        lockRequest.getLockId(),
        lockRequest.getInstanceId(),
        acquiredAt,
        releaseAt
    );
  }

  private final LockId id;
  private final InstanceId instanceId;
  private final Instant acquiredAt;
  private final Instant expiresAt;

  private MongoDistributedLock(
      LockId id,
      InstanceId instanceId,
      Instant createdAt,
      Instant expiresAt) {
    this.id = expectNonNull(id);
    this.instanceId = expectNonNull(instanceId);
    this.acquiredAt = expectNonNull(createdAt);
    this.expiresAt = expiresAt;
  }

  Document toDocument() {
    Document result = new Document()
        .append(LOCK_ID_FIELD, id.getValue())
        .append(ACQUIRED_BY_FIELD, instanceId.getValue())
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
        ", instanceId=" + instanceId +
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
        Objects.equals(instanceId, that.instanceId) &&
        Objects.equals(acquiredAt, that.acquiredAt) &&
        Objects.equals(expiresAt, that.expiresAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, instanceId, acquiredAt, expiresAt);
  }
}
