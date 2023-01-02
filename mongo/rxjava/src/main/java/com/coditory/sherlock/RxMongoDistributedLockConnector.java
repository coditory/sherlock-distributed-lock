package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import com.mongodb.MongoCommandException;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Clock;
import java.time.Instant;

import static com.coditory.sherlock.MongoDistributedLockQueries.*;
import static com.coditory.sherlock.Preconditions.expectNonNull;

class RxMongoDistributedLockConnector implements RxDistributedLockConnector {
    private static final int DUPLICATE_KEY_ERROR_CODE = 11000;
    private static final FindOneAndReplaceOptions upsertOptions = new FindOneAndReplaceOptions()
        .upsert(true)
        .returnDocument(ReturnDocument.AFTER);
    private final RxMongoCollectionInitializer collectionInitializer;
    private final Clock clock;

    RxMongoDistributedLockConnector(MongoCollection<Document> collection, Clock clock) {
        expectNonNull(collection, "Expected non null collection");
        this.collectionInitializer = new RxMongoCollectionInitializer(collection);
        this.clock = expectNonNull(clock, "Expected non null clock");
    }

    @Override
    public Single<InitializationResult> initialize() {
        return collectionInitializer.getInitializedCollection()
            .map(collection -> InitializationResult.of(true))
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not initialize Mongo collection", e)));
    }

    @Override
    public Single<AcquireResult> acquire(LockRequest lockRequest) {
        Instant now = now();
        return upsert(
            queryReleased(lockRequest.getLockId(), now),
            MongoDistributedLock.fromLockRequest(lockRequest, now)
        )
            .map(AcquireResult::of)
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not acquire lock: " + lockRequest, e))
            );
    }

    @Override
    public Single<AcquireResult> acquireOrProlong(LockRequest lockRequest) {
        Instant now = now();
        return upsert(
            queryAcquiredOrReleased(lockRequest.getLockId(), lockRequest.getOwnerId(), now),
            MongoDistributedLock.fromLockRequest(lockRequest, now)
        )
            .map(AcquireResult::of)
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not acquire or prolong lock: " + lockRequest, e)));
    }

    @Override
    public Single<AcquireResult> forceAcquire(LockRequest lockRequest) {
        return upsert(
            queryById(lockRequest.getLockId()),
            MongoDistributedLock.fromLockRequest(lockRequest, now())
        )
            .map(AcquireResult::of)
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not force acquire lock: " + lockRequest, e)));
    }

    @Override
    public Single<ReleaseResult> release(LockId lockId, OwnerId ownerId) {
        return delete(queryAcquired(lockId, ownerId))
            .map(ReleaseResult::of)
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not release lock: " + lockId.getValue() + ", owner: " + ownerId, e)));
    }

    @Override
    public Single<ReleaseResult> forceRelease(LockId lockId) {
        return delete(queryById(lockId))
            .map(ReleaseResult::of)
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not force release lock: " + lockId.getValue(), e)));
    }

    @Override
    public Single<ReleaseResult> forceReleaseAll() {
        return deleteAll()
            .map(ReleaseResult::of)
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not force release all locks", e)));
    }

    private Single<Boolean> deleteAll() {
        return getLockCollection()
            .flatMapMaybe(collection -> Flowable.fromPublisher(collection.deleteMany(new BsonDocument())).firstElement())
            .map(result -> result.getDeletedCount() > 0)
            .switchIfEmpty(Single.just(false));
    }

    private Single<Boolean> delete(Bson query) {
        return getLockCollection()
            .flatMapMaybe(collection -> Flowable.fromPublisher(collection.findOneAndDelete(query)).firstElement())
            .map(MongoDistributedLock::fromDocument)
            .map(lock -> lock.isActive(now()))
            .switchIfEmpty(Single.just(false));
    }

    private Single<Boolean> upsert(Bson query, MongoDistributedLock lock) {
        return getLockCollection()
            .flatMapMaybe(collection -> Flowable.fromPublisher(collection.findOneAndReplace(query, lock.toDocument(), upsertOptions)).firstElement())
            .map(document -> MongoDistributedLock.fromDocument(document).equals(lock))
            .switchIfEmpty(Single.just(false))
            .onErrorResumeNext(exception ->
                exception instanceof MongoCommandException
                    && ((MongoCommandException) exception).getErrorCode() == DUPLICATE_KEY_ERROR_CODE
                    ? Single.just(false)
                    : Single.error(exception)
            );
    }

    private Instant now() {
        return clock.instant();
    }

    private Single<MongoCollection<Document>> getLockCollection() {
        return collectionInitializer.getInitializedCollection();
    }
}
