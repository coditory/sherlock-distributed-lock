package com.coditory.xlock.mongo;

import com.coditory.xlock.common.AcquisitionId;
import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.driver.LockRequest;
import com.coditory.xlock.common.driver.UnlockResult;
import com.coditory.xlock.common.driver.XLockDriver;
import com.coditory.xlock.common.driver.LockResult;
import com.coditory.xlock.common.LockState;
import com.coditory.xlock.mongo.MongoLockState.Fields;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static com.coditory.xlock.common.driver.LockResult.lockGranted;
import static com.coditory.xlock.common.driver.LockResult.lockRefused;
import static com.coditory.xlock.common.driver.UnlockResult.unlockFailure;
import static com.coditory.xlock.common.driver.UnlockResult.unlockSuccess;
import static com.coditory.xlock.common.util.XLockPreconditions.expectNonEmpty;
import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

public class XLockMongoDriver implements XLockDriver {
  private static final FindOneAndUpdateOptions upsertOptions = new FindOneAndUpdateOptions()
      .upsert(true)
      .returnDocument(ReturnDocument.AFTER);
  private final MongoClient mongoClient;
  private final String databaseName;
  private final String collectionName;
  private final Clock clock;

  public XLockMongoDriver(
      MongoClient client, String databaseName, String collectionName, Clock clock) {
    this.mongoClient = expectNonNull(client, "Expected non null mongoClient");
    this.databaseName = expectNonEmpty(databaseName, "Expected non empty databaseName");
    this.collectionName = expectNonEmpty(collectionName, "Expected non empty collectionName");
    this.clock = expectNonNull(clock, "Expected non null clock");
  }

  @Override
  public LockResult lock(LockRequest lockRequest) {
    Instant now = now();
    return lock(
        queryLock(now, lockRequest),
        now,
        lockRequest
    );
  }

  @Override
  public LockResult forceLock(LockRequest lockRequest) {
    return lock(
        queryLock(lockRequest.getLockId()),
        now(),
        lockRequest
    );
  }

  private LockResult lock(Bson query, Instant now, LockRequest lockRequest) {
    MongoLockState newLockDocument = mongoLockState(now, lockRequest);
    LockState newLock = newLockDocument.toLockState();
    return upsert(query, newLockDocument)
        ? lockGranted(newLock.getLockId(), newLock.getAcquisitionId()) // TODO: Return full lock state
        : lockRefused(newLock.getLockId());
  }

  @Override
  public UnlockResult unlock(LockId lockId) {
    return delete(queryLock(now(), lockId)) ? unlockSuccess() : unlockFailure();
  }

  @Override
  public UnlockResult unlock(AcquisitionId acquisitionId) {
    return delete(queryLock(now(), acquisitionId)) ? unlockSuccess() : unlockFailure();
  }

  @Override
  public UnlockResult forceUnlock(LockId lockId) {
    return delete(queryLock(lockId)) ? unlockSuccess() : unlockFailure();
  }

  @Override
  public UnlockResult forceUnlock(AcquisitionId acquisitionId) {
    return delete(queryLock(acquisitionId)) ? unlockSuccess() : unlockFailure();
  }

  @Override
  public Optional<LockState> getLockState(LockId lockId) {
    Document document = getLockCollection().find(queryById(lockId))
        .first();
    return Optional.ofNullable(document)
        // TODO: Drop one mapping
        .map(MongoLockState::fromDocument)
        .map(MongoLockState::toLockState);
  }

  private boolean delete(Bson query) {
    Document deleted = getLockCollection().findOneAndDelete(query);
    return deleted != null;
  }

  private boolean upsert(Bson query, MongoLockState lockState) {
    Document current = getLockCollection().findOneAndUpdate(query, lockState.toBsonDocument(), upsertOptions);
    return current != null && MongoLockState.fromDocument(current).hasSameAcquisitionId(lockState);
  }

  private Bson queryLock(Instant now, LockRequest lockRequest) {
    return Filters.and(
        queryLock(lockRequest.getLockId()),
        queryAfterRelease(now)
    );
  }

  private Bson queryLock(Instant now, LockId lockId) {
    return Filters.and(
        queryLock(lockId),
        queryAfterRelease(now)
    );
  }

  private Bson queryLock(LockId lockId) {
    return Filters.eq(Fields.LOCK_ID_FIELD, lockId.getValue());
  }

  private Bson queryLock(Instant now, AcquisitionId acquisitionId) {
    return Filters.and(
        queryLock(acquisitionId),
        queryAfterRelease(now)
    );
  }

  private Bson queryAfterRelease(Instant now) {
    return Filters.lte(Fields.RELEASE_AT_FIELD, now);
  }

  private Bson queryLock(AcquisitionId acquisitionId) {
    return Filters.eq(Fields.ACQUISITION_ID_FIELD, acquisitionId.getValue());
  }

  private Instant now() {
    return clock.instant();
  }

  private MongoLockState mongoLockState(Instant now, LockRequest lockRequest) {
    LockId lockId = lockRequest.getLockId();
    return new MongoLockState(
        lockRequest.getLockId().getValue(),
        AcquisitionId.unqueLockAcquisitionId(lockId).getValue(),
        lockRequest.getInstanceId().getValue(),
        now,
        lockRequest.expireAt(now).orElse(null)
    );
  }

  private Bson queryById(LockId lockId) {
    return Filters.eq("_id", lockId.getValue());
  }

  private MongoCollection<Document> getLockCollection() {
    return mongoClient.getDatabase(databaseName)
        .getCollection(collectionName);
  }
}
