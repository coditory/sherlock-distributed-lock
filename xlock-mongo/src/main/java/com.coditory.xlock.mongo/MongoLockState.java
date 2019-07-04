package com.coditory.xlock.mongo;

import com.coditory.xlock.common.AcquisitionId;
import com.coditory.xlock.common.InstanceId;
import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.LockState;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import static com.coditory.xlock.mongo.MongoLockState.Fields.ACQUISITION_ID_FIELD;
import static com.coditory.xlock.mongo.MongoLockState.Fields.ACQUIRED_AT_FIELD;
import static com.coditory.xlock.mongo.MongoLockState.Fields.LOCK_ID_FIELD;
import static com.coditory.xlock.mongo.MongoLockState.Fields.RELEASE_AT_FIELD;
import static com.coditory.xlock.mongo.MongoLockState.Fields.INSTANCE_ID_FIELD;

class MongoLockState {
  static MongoLockState fromDocument(Document document) {
    return new MongoLockState(
        document.getString(LOCK_ID_FIELD),
        document.getString(ACQUISITION_ID_FIELD),
        document.getString(INSTANCE_ID_FIELD),
        dateToInstant(document.getDate(ACQUIRED_AT_FIELD)),
        dateToInstant(document.getDate(RELEASE_AT_FIELD))
    );
  }

  private static Instant dateToInstant(Date date) {
    return date != null ? date.toInstant() : null;
  }

  interface Fields {
    String LOCK_ID_FIELD = "_id";
    String ACQUISITION_ID_FIELD = "acquisitionId";
    String INSTANCE_ID_FIELD = "instanceId";
    String ACQUIRED_AT_FIELD = "acquiredAt";
    String RELEASE_AT_FIELD = "releaseAt";
  }

  private final String lockId;
  private final String acquisitionId;
  private final String instanceId;
  private final Instant acquiredAt;
  private final Instant releaseAt;

  MongoLockState(
      String lockId,
      String acquisitionId,
      String instanceId,
      Instant acquiredAt,
      Instant releaseAt) {
    this.lockId = lockId;
    this.acquisitionId = acquisitionId;
    this.instanceId = instanceId;
    this.acquiredAt = acquiredAt;
    this.releaseAt = releaseAt;
  }

  LockState toLockState() {
    return new LockState(
        new LockId(lockId),
        new AcquisitionId(acquisitionId),
        new InstanceId(instanceId),
        acquiredAt,
        releaseAt
    );
  }

  BsonDocument toBsonDocument() {
    return new BsonDocument()
        .append(LOCK_ID_FIELD, new BsonString(lockId))
        .append(Fields.ACQUISITION_ID_FIELD, new BsonString(acquisitionId))
        .append(Fields.INSTANCE_ID_FIELD, new BsonString(instanceId))
        .append(Fields.ACQUIRED_AT_FIELD, new BsonDateTime(acquiredAt.toEpochMilli()))
        .append(Fields.RELEASE_AT_FIELD, new BsonDateTime(releaseAt.toEpochMilli()));

  }

  boolean hasSameAcquisitionId(MongoLockState other) {
    return other != null && Objects.equals(other.acquisitionId, acquisitionId);
  }

  @Override
  public String toString() {
    return "MongoLockState{" +
        "lockId='" + lockId + '\'' +
        ", acquisitionId='" + acquisitionId + '\'' +
        ", instanceId='" + instanceId + '\'' +
        ", acquiredAt=" + acquiredAt +
        ", releaseAt=" + releaseAt +
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
    MongoLockState that = (MongoLockState) o;
    return Objects.equals(lockId, that.lockId) &&
        Objects.equals(acquisitionId, that.acquisitionId) &&
        Objects.equals(instanceId, that.instanceId) &&
        Objects.equals(acquiredAt, that.acquiredAt) &&
        Objects.equals(releaseAt, that.releaseAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockId, acquisitionId, instanceId, acquiredAt, releaseAt);
  }
}
