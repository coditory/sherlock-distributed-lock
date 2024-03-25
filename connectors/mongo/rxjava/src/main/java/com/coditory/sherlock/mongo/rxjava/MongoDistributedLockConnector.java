package com.coditory.sherlock.mongo.rxjava;

import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.SherlockException;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import com.coditory.sherlock.mongo.MongoDistributedLock;
import com.coditory.sherlock.rxjava.DistributedLockConnector;
import com.mongodb.MongoCommandException;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.mongo.MongoDistributedLockQueries.*;

class MongoDistributedLockConnector implements DistributedLockConnector {
    private static final int DUPLICATE_KEY_ERROR_CODE = 11000;
    private static final FindOneAndReplaceOptions upsertOptions = new FindOneAndReplaceOptions()
        .upsert(true)
        .returnDocument(ReturnDocument.AFTER);
    private final MongoCollectionInitializer collectionInitializer;
    private final Clock clock;

    MongoDistributedLockConnector(MongoCollection<Document> collection, Clock clock) {
        expectNonNull(collection, "collection");
        this.collectionInitializer = new MongoCollectionInitializer(collection);
        this.clock = expectNonNull(clock, "clock");
    }

    @Override
    @NotNull
    public Single<InitializationResult> initialize() {
        return collectionInitializer.getInitializedCollection()
            .map(collection -> InitializationResult.of(true))
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not initialize Mongo collection", e)));
    }

    @Override
    @NotNull
    public Single<AcquireResult> acquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        Instant now = now();
        return upsert(
            queryReleased(lockRequest.lockId(), now),
            MongoDistributedLock.fromLockRequest(lockRequest, now)
        )
            .map(AcquireResult::of)
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not acquire lock: " + lockRequest, e))
            );
    }

    @Override
    @NotNull
    public Single<AcquireResult> acquireOrProlong(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        Instant now = now();
        return upsert(
            queryAcquiredOrReleased(lockRequest.lockId(), lockRequest.ownerId(), now),
            MongoDistributedLock.fromLockRequest(lockRequest, now)
        )
            .map(AcquireResult::of)
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not acquire or prolong lock: " + lockRequest, e)));
    }

    @Override
    @NotNull
    public Single<AcquireResult> forceAcquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return upsert(
            queryById(lockRequest.lockId()),
            MongoDistributedLock.fromLockRequest(lockRequest, now())
        )
            .map(AcquireResult::of)
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not force acquire lock: " + lockRequest, e)));
    }

    @Override
    @NotNull
    public Single<ReleaseResult> release(@NotNull String lockId, @NotNull String ownerId) {
        expectNonNull(lockId, "lockId");
        expectNonNull(ownerId, "ownerId");
        return delete(queryAcquired(lockId, ownerId))
            .map(ReleaseResult::of)
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not release lock: " + lockId + ", owner: " + ownerId, e)));
    }

    @Override
    @NotNull
    public Single<ReleaseResult> forceRelease(@NotNull String lockId) {
        expectNonNull(lockId, "lockId");
        return delete(queryById(lockId))
            .map(ReleaseResult::of)
            .onErrorResumeNext(e -> Single.error(new SherlockException("Could not force release lock: " + lockId, e)));
    }

    @Override
    @NotNull
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
