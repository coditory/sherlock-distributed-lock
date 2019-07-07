package com.coditory.distributed.lock.mongo;

import com.coditory.distributed.lock.DistributedLockDriver;
import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;
import com.coditory.distributed.lock.common.LockRequest;
import com.coditory.distributed.lock.common.MongoDistributedLock;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.distributed.lock.common.MongoDistributedLock.INDEX;
import static com.coditory.distributed.lock.common.MongoDistributedLock.INDEX_OPTIONS;
import static com.coditory.distributed.lock.common.MongoDistributedLockQueries.queryAcquired;
import static com.coditory.distributed.lock.common.MongoDistributedLockQueries.queryAcquiredAndReleased;
import static com.coditory.distributed.lock.common.MongoDistributedLockQueries.queryAcquiredOrReleased;
import static com.coditory.distributed.lock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;

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
  public boolean acquire(LockRequest lockRequest) {
    Instant now = now();
    return upsert(
        queryAcquiredAndReleased(lockRequest.getLockId(), lockRequest.getInstanceId(), now),
        MongoDistributedLock.fromLockRequest(lockRequest, now)
    );
  }

  @Override
  public boolean acquireOrProlong(LockRequest lockRequest) {
    Instant now = now();
    return upsert(
        queryAcquiredOrReleased(lockRequest.getLockId(), lockRequest.getInstanceId(), now),
        MongoDistributedLock.fromLockRequest(lockRequest, now)
    );
  }

  @Override
  public boolean forceAcquire(LockRequest lockRequest) {
    return upsert(
        queryAcquired(lockRequest.getLockId()),
        MongoDistributedLock.fromLockRequest(lockRequest, now())
    );
  }

  @Override
  public boolean release(LockId lockId, InstanceId instanceId) {
    return delete(queryAcquired(lockId, instanceId));
  }

  @Override
  public boolean forceRelease(LockId lockId) {
    return delete(queryAcquired(lockId));
  }

  @Override
  public void forceReleaseAll() {
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
    collection.createIndex(INDEX, INDEX_OPTIONS);
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
