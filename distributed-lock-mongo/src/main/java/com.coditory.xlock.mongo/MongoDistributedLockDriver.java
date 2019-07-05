package com.coditory.xlock.mongo;

import com.coditory.xlock.common.InstanceId;
import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.driver.LockRequest;
import com.coditory.xlock.common.driver.DistributedLockDriver;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReturnDocument;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.xlock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.xlock.common.util.Preconditions.expectNonNull;
import static com.coditory.xlock.mongo.MongoDistributedLock.Fields.ACQUIRED_AT_FIELD;
import static com.coditory.xlock.mongo.MongoDistributedLock.Fields.ACQUIRED_BY_FIELD;
import static com.coditory.xlock.mongo.MongoDistributedLock.Fields.LOCK_ID_FIELD;
import static com.coditory.xlock.mongo.MongoDistributedLock.Fields.EXPIRES_AT_FIELD;

public class MongoDistributedLockDriver implements DistributedLockDriver {
  private static final int DUPLICATE_KEY_ERROR_CODE = 11000;
  private static final FindOneAndReplaceOptions upsertOptions = new FindOneAndReplaceOptions()
      .upsert(true)
      .returnDocument(ReturnDocument.AFTER);
  private final MongoClient mongoClient;
  private final String databaseName;
  private final String collectionName;
  private final Clock clock;
  private final AtomicBoolean indexesCreated = new AtomicBoolean();

  public MongoDistributedLockDriver(
      MongoClient client, String databaseName, String collectionName, Clock clock) {
    this.mongoClient = expectNonNull(client, "Expected non null mongoClient");
    this.databaseName = expectNonEmpty(databaseName, "Expected non empty databaseName");
    this.collectionName = expectNonEmpty(collectionName, "Expected non empty collectionName");
    this.clock = expectNonNull(clock, "Expected non null clock");
  }

  @Override
  public void initialize() {
    boolean shouldCreateIndexes = indexesCreated.compareAndSet(false, true);
    if (shouldCreateIndexes) {
      createIndexes();
    }
  }

  @Override
  public boolean lock(LockRequest lockRequest) {
    Instant now = now();
    return upsert(
        queryReleased(lockRequest.getLockId(), lockRequest.getInstanceId(), now),
        MongoDistributedLock.fromLockRequest(lockRequest, now)
    );
  }

  @Override
  public boolean lockOrRelock(LockRequest lockRequest) {
    return upsert(
        queryLock(lockRequest.getLockId(), lockRequest.getInstanceId()),
        MongoDistributedLock.fromLockRequest(lockRequest, now())
    );
  }

  @Override
  public boolean forceLock(LockRequest lockRequest) {
    return upsert(
        queryLock(lockRequest.getLockId()),
        MongoDistributedLock.fromLockRequest(lockRequest, now())
    );
  }

  @Override
  public boolean unlock(LockId lockId, InstanceId instanceId) {
    return delete(queryLock(lockId, instanceId));
  }

  @Override
  public boolean forceUnlock(LockId lockId) {
    return delete(queryLock(lockId));
  }

  @Override
  public void forceUnlockAll() {
    deleteAll();
  }

  private boolean delete(Bson query) {
    Document deleted = getLockCollection().findOneAndDelete(query);
    return deleted != null;
  }

  private void deleteAll() {
    getLockCollection().deleteMany(new BsonDocument());
  }

  private boolean upsert(Bson query, MongoDistributedLock lock) {
    Document documentToUpsert = lock.toDocument();
    try {
      Document current = getLockCollection()
          .findOneAndReplace(query, documentToUpsert, upsertOptions);
      return current != null && MongoDistributedLock.fromDocument(current).equals(lock);
    } catch (MongoCommandException exception) {
      if (exception.getErrorCode() != DUPLICATE_KEY_ERROR_CODE) {
        throw exception;
      }
      return false;
    }
  }

  private void createIndexes() {
    indexesCreated.set(true);
    MongoCollection<Document> collection = getLockCollection();
    collection.createIndex(
        Indexes.ascending(LOCK_ID_FIELD, ACQUIRED_BY_FIELD, ACQUIRED_AT_FIELD),
        new IndexOptions().background(true));
  }

  private Bson queryReleased(LockId lockId, InstanceId instanceId, Instant now) {
    return Filters.and(
        queryLock(lockId),
        queryLock(instanceId),
        queryReleased(now)
    );
  }

  private Bson queryLock(LockId lockId, InstanceId instanceId) {
    return Filters.and(
        queryLock(lockId),
        queryLock(instanceId)
    );
  }

  private Bson queryLock(LockId lockId) {
    return Filters.eq(LOCK_ID_FIELD, lockId.getValue());
  }

  private Bson queryLock(InstanceId instanceId) {
    return Filters.eq(ACQUIRED_BY_FIELD, instanceId.getValue());
  }

  private Bson queryReleased(Instant now) {
    return Filters.lte(EXPIRES_AT_FIELD, now);
  }

  private Instant now() {
    return clock.instant();
  }

  private MongoCollection<Document> getLockCollection() {
    initialize();
    return mongoClient.getDatabase(databaseName)
        .getCollection(collectionName);
  }
}
