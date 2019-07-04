package com.coditory.xlock.mongo;

import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.LockInstanceId;
import com.coditory.xlock.common.LockState;
import com.coditory.xlock.common.ServiceInstanceId;
import org.bson.Document;

import java.time.Instant;
import java.util.Date;

import static com.coditory.xlock.mongo.LockStateMongoMapper.Fields.ACQUIRED_AT_FIELD;
import static com.coditory.xlock.mongo.LockStateMongoMapper.Fields.LOCK_ID_FIELD;
import static com.coditory.xlock.mongo.LockStateMongoMapper.Fields.LOCK_INSTANCE_ID_FIELD;
import static com.coditory.xlock.mongo.LockStateMongoMapper.Fields.RELEASE_AT_FIELD;
import static com.coditory.xlock.mongo.LockStateMongoMapper.Fields.SERVICE_INSTANCE_ID_FIELD;

class LockStateMongoMapper {
  interface Fields {
    String LOCK_ID_FIELD = "_id";
    String LOCK_INSTANCE_ID_FIELD = "lockInstanceId";
    String SERVICE_INSTANCE_ID_FIELD = "serviceInstanceId";
    String ACQUIRED_AT_FIELD = "acquiredAt";
    String RELEASE_AT_FIELD = "releaseAt";
  }

  static Document toDocument(LockState lockState) {
    Document result = new Document()
        .append(LOCK_ID_FIELD, lockState.getLockId().getValue())
        .append(LOCK_INSTANCE_ID_FIELD, lockState.getLockInstanceId().getValue())
        .append(SERVICE_INSTANCE_ID_FIELD, lockState.getServiceInstanceId().getValue())
        .append(ACQUIRED_AT_FIELD, lockState.getAcquiredAt());
    if (lockState.getReleaseAt().isPresent()) {
      result = result.append(RELEASE_AT_FIELD, lockState.getReleaseAt().get());
    }
    return result;
  }

  static LockState fromDocument(Document document) {
    return new LockState(
        LockId.of(document.getString(LOCK_ID_FIELD)),
        LockInstanceId.of(document.getString(LOCK_INSTANCE_ID_FIELD)),
        ServiceInstanceId.of(document.getString(SERVICE_INSTANCE_ID_FIELD)),
        dateToInstant(document.getDate(ACQUIRED_AT_FIELD)),
        dateToInstant(document.getDate(RELEASE_AT_FIELD))
    );
  }

  private static Instant dateToInstant(Date date) {
    return date != null ? date.toInstant() : null;
  }
}
