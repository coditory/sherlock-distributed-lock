package com.coditory.xlock.mongo;

import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.LockInstanceId;
import com.coditory.xlock.common.LockState;
import com.coditory.xlock.common.driver.LockRequest;
import com.coditory.xlock.common.driver.LockResult;
import com.coditory.xlock.common.driver.UnlockResult;
import com.coditory.xlock.common.driver.XLockDriver;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.xlock.common.driver.LockResult.lockGranted;
import static com.coditory.xlock.common.driver.LockResult.lockRefused;
import static com.coditory.xlock.common.driver.UnlockResult.unlockFailure;
import static com.coditory.xlock.common.driver.UnlockResult.unlockSuccess;
import static com.coditory.xlock.common.util.XLockPreconditions.expectNonEmpty;
import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;
import static com.coditory.xlock.mongo.LockStateMongoMapper.Fields.ACQUIRED_AT_FIELD;
import static com.coditory.xlock.mongo.LockStateMongoMapper.Fields.LOCK_ID_FIELD;
import static com.coditory.xlock.mongo.LockStateMongoMapper.Fields.LOCK_INSTANCE_ID_FIELD;
import static com.coditory.xlock.mongo.LockStateMongoMapper.Fields.RELEASE_AT_FIELD;

public class XLockMongoDriver implements XLockDriver {
  private static final int DUPLICATE_KEY_ERROR_CODE = 11000;
  private static final FindOneAndReplaceOptions upsertOptions = new FindOneAndReplaceOptions()
      .upsert(true)
      .returnDocument(ReturnDocument.AFTER);
  private final MongoClient mongoClient;
  private final String databaseName;
  private final String collectionName;
  private final Clock clock;
  private final AtomicBoolean createdIndexes = new AtomicBoolean();

  public XLockMongoDriver(
      MongoClient client, String databaseName, String collectionName, Clock clock) {
    this.mongoClient = expectNonNull(client, "Expected non null mongoClient");
    this.databaseName = expectNonEmpty(databaseName, "Expected non empty databaseName");
    this.collectionName = expectNonEmpty(collectionName, "Expected non empty collectionName");
    this.clock = expectNonNull(clock, "Expected non null clock");
  }

  @Override
  public void prepare() {
    boolean createIndexes = createdIndexes.compareAndSet(false, true);
    if (createIndexes) {
      MongoCollection<Document> collection = getLockCollection();
      collection.createIndex(
          Indexes.ascending(LOCK_INSTANCE_ID_FIELD),
          new IndexOptions().unique(true).background(true));
      collection.createIndex(
          Indexes.ascending(RELEASE_AT_FIELD),
          new IndexOptions().background(true));
      collection.createIndex(
          Indexes.ascending(ACQUIRED_AT_FIELD),
          new IndexOptions().background(true));
    }
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
    LockState newLock = LockState.fromLockRequest(lockRequest, now);
    return upsert(query, newLock)
        ? lockGranted()
        : lockRefused();
  }

  @Override
  public UnlockResult unlock(LockInstanceId lockInstanceId) {
    return delete(queryLock(lockInstanceId)) ? unlockSuccess() : unlockFailure();
  }

  @Override
  public UnlockResult forceUnlock(LockId lockId) {
    return delete(queryLock(lockId)) ? unlockSuccess() : unlockFailure();
  }

  @Override
  public Optional<LockState> getLockState(LockId lockId) {
    Document document = getLockCollection().find(queryById(lockId))
        .first();
    return Optional.ofNullable(document)
        .map(LockStateMongoMapper::fromDocument);
  }

  private boolean delete(Bson query) {
    Document deleted = getLockCollection().findOneAndDelete(query);
    return deleted != null;
  }

  private boolean upsert(Bson query, LockState lockState) {
    Document documentToUpsert = LockStateMongoMapper.toDocument(lockState);
    try {
      Document current = getLockCollection()
          .findOneAndReplace(query, documentToUpsert, upsertOptions);
      return current != null && LockStateMongoMapper.fromDocument(current).equals(lockState);
    } catch (MongoCommandException exception) {
      if (exception.getErrorCode() != DUPLICATE_KEY_ERROR_CODE) {
        throw exception;
      }
      return false;
    }
  }

  private Bson queryLock(Instant now, LockRequest lockRequest) {
    return Filters.and(
        queryLock(lockRequest.getLockId()),
        Filters.or(queryAfterRelease(now), queryLock(lockRequest.getLockInstanceId()))
    );
  }

  private Bson queryLock(LockId lockId) {
    return Filters.eq(LOCK_ID_FIELD, lockId.getValue());
  }

  private Bson queryAfterRelease(Instant now) {
    return Filters.lte(RELEASE_AT_FIELD, now);
  }

  private Bson queryLock(LockInstanceId lockInstanceId) {
    return Filters.eq(LOCK_INSTANCE_ID_FIELD, lockInstanceId.getValue());
  }

  private Instant now() {
    return clock.instant();
  }

  private Bson queryById(LockId lockId) {
    return Filters.eq("_id", lockId.getValue());
  }

  private MongoCollection<Document> getLockCollection() {
    prepare();
    return mongoClient.getDatabase(databaseName)
        .getCollection(collectionName);
  }
}
