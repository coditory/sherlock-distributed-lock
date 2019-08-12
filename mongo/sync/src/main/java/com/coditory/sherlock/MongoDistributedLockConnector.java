package com.coditory.sherlock;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Clock;
import java.time.Instant;

import static com.coditory.sherlock.MongoDistributedLock.fromLockRequest;
import static com.coditory.sherlock.MongoDistributedLockQueries.queryAcquired;
import static com.coditory.sherlock.MongoDistributedLockQueries.queryAcquiredOrReleased;
import static com.coditory.sherlock.MongoDistributedLockQueries.queryById;
import static com.coditory.sherlock.MongoDistributedLockQueries.queryReleased;
import static com.coditory.sherlock.Preconditions.expectNonNull;

class MongoDistributedLockConnector implements DistributedLockConnector {
  private static final int DUPLICATE_KEY_ERROR_CODE = 11000;
  private static final FindOneAndReplaceOptions upsertOptions = new FindOneAndReplaceOptions()
    .upsert(true)
    .returnDocument(ReturnDocument.AFTER);
  private final MongoCollectionInitializer collectionInitializer;
  private final Clock clock;

  MongoDistributedLockConnector(
    MongoCollection<Document> collection, Clock clock) {
    expectNonNull(collection, "Expected non null collection");
    this.collectionInitializer = new MongoCollectionInitializer(collection);
    this.clock = expectNonNull(clock, "Expected non null clock");
  }

  @Override
  public void initialize() {
    collectionInitializer.getInitializedCollection();
  }

  @Override
  public boolean acquire(LockRequest lockRequest) {
    Instant now = now();
    return upsert(
      queryReleased(lockRequest.getLockId(), now),
      fromLockRequest(lockRequest, now)
    );
  }

  @Override
  public boolean acquireOrProlong(LockRequest lockRequest) {
    Instant now = now();
    return upsert(
      queryAcquiredOrReleased(lockRequest.getLockId(), lockRequest.getOwnerId(), now),
      fromLockRequest(lockRequest, now)
    );
  }

  @Override
  public boolean forceAcquire(LockRequest lockRequest) {
    return upsert(
      queryById(lockRequest.getLockId()),
      fromLockRequest(lockRequest, now())
    );
  }

  @Override
  public boolean release(LockId lockId, OwnerId ownerId) {
    return delete(queryAcquired(lockId, ownerId));
  }

  @Override
  public boolean forceRelease(LockId lockId) {
    return delete(queryById(lockId));
  }

  @Override
  public boolean forceReleaseAll() {
    return deleteAll();
  }

  private boolean deleteAll() {
    DeleteResult result = getLockCollection().deleteMany(new BsonDocument());
    return result != null && result.getDeletedCount() > 0;
  }

  private boolean delete(Bson query) {
    Document deleted = getLockCollection().findOneAndDelete(query);
    return deleted != null
      && MongoDistributedLock.fromDocument(deleted).isActive(now());
  }

  private boolean upsert(Bson query, MongoDistributedLock lock) {
    Document documentToUpsert = lock.toDocument();
    try {
      Document current = getLockCollection()
        .findOneAndReplace(query, documentToUpsert, upsertOptions);
      return lock.hasSameOwner(current);
    } catch (MongoCommandException exception) {
      if (exception.getErrorCode() != DUPLICATE_KEY_ERROR_CODE) {
        throw exception;
      }
      return false;
    }
  }

  private Instant now() {
    return clock.instant();
  }

  private MongoCollection<Document> getLockCollection() {
    return collectionInitializer.getInitializedCollection();
  }
}
