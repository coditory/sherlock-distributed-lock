package com.coditory.sherlock;

import com.coditory.sherlock.common.InstanceId;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.LockRequest;
import com.coditory.sherlock.common.MongoDistributedLock;
import com.coditory.sherlock.common.MongoDistributedLockQueries;
import com.coditory.sherlock.common.util.Preconditions;
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

import static com.coditory.sherlock.common.MongoDistributedLockQueries.queryAcquired;
import static com.coditory.sherlock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

class MongoDistributedLockDriver implements DistributedLockDriver {
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
    this.mongoClient = Preconditions.expectNonNull(client, "Expected non null mongoClient");
    this.databaseName = Preconditions.expectNonEmpty(databaseName, "Expected non empty databaseName");
    this.collectionName = Preconditions
        .expectNonEmpty(collectionName, "Expected non empty collectionName");
    this.clock = Preconditions.expectNonNull(clock, "Expected non null clock");
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
        MongoDistributedLockQueries
            .queryAcquiredAndReleased(lockRequest.getLockId(), lockRequest.getInstanceId(), now),
        MongoDistributedLock.fromLockRequest(lockRequest, now)
    );
  }

  @Override
  public boolean acquireOrProlong(LockRequest lockRequest) {
    Instant now = now();
    return upsert(
        MongoDistributedLockQueries
            .queryAcquiredOrReleased(lockRequest.getLockId(), lockRequest.getInstanceId(), now),
        MongoDistributedLock.fromLockRequest(lockRequest, now)
    );
  }

  @Override
  public boolean forceAcquire(LockRequest lockRequest) {
    return upsert(
        MongoDistributedLockQueries.queryAcquired(lockRequest.getLockId()),
        MongoDistributedLock.fromLockRequest(lockRequest, now())
    );
  }

  @Override
  public boolean release(LockId lockId, InstanceId instanceId) {
    return delete(MongoDistributedLockQueries.queryAcquired(lockId, instanceId));
  }

  @Override
  public boolean forceRelease(LockId lockId) {
    return delete(MongoDistributedLockQueries.queryAcquired(lockId));
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
    collection.createIndex(MongoDistributedLock.INDEX, MongoDistributedLock.INDEX_OPTIONS);
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
