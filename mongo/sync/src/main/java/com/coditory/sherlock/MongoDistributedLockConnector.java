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
import static com.coditory.sherlock.MongoDistributedLockQueries.*;
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
        try {
            collectionInitializer.getInitializedCollection();
        } catch (Throwable e) {
            throw new SherlockException("Could not initialize Mongo collection", e);
        }
    }

    @Override
    public boolean acquire(LockRequest lockRequest) {
        Instant now = now();
        try {
            return upsert(
                    queryReleased(lockRequest.getLockId(), now),
                    fromLockRequest(lockRequest, now)
            );
        } catch (Throwable e) {
            throw new SherlockException("Could not acquire lock: " + lockRequest, e);
        }
    }

    @Override
    public boolean acquireOrProlong(LockRequest lockRequest) {
        Instant now = now();
        try {
            return upsert(
                    queryAcquiredOrReleased(lockRequest.getLockId(), lockRequest.getOwnerId(), now),
                    fromLockRequest(lockRequest, now)
            );
        } catch (Throwable e) {
            throw new SherlockException("Could not acquire or prolong lock: " + lockRequest, e);
        }
    }

    @Override
    public boolean forceAcquire(LockRequest lockRequest) {
        try {
            return upsert(
                    queryById(lockRequest.getLockId()),
                    fromLockRequest(lockRequest, now())
            );
        } catch (Throwable e) {
            throw new SherlockException("Could not acquire or prolong lock: " + lockRequest, e);
        }
    }

    @Override
    public boolean release(LockId lockId, OwnerId ownerId) {
        try {
            return delete(queryAcquired(lockId, ownerId));
        } catch (Throwable e) {
            throw new SherlockException("Could not release lock: " + lockId.getValue() + ", owner: " + ownerId, e);
        }
    }

    @Override
    public boolean forceRelease(LockId lockId) {
        try {
            return delete(queryById(lockId));
        } catch (Throwable e) {
            throw new SherlockException("Could not force release lock: " + lockId.getValue(), e);
        }
    }

    @Override
    public boolean forceReleaseAll() {
        try {
            return deleteAll();
        } catch (Throwable e) {
            throw new SherlockException("Could not force release all locks", e);
        }
    }

    private boolean deleteAll() {
        DeleteResult result = getLockCollection().deleteMany(new BsonDocument());
        return result.getDeletedCount() > 0;
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
