package com.coditory.sherlock.mongo.reactor;

import com.coditory.sherlock.LockId;
import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.OwnerId;
import com.coditory.sherlock.SherlockException;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import com.coditory.sherlock.mongo.MongoDistributedLock;
import com.coditory.sherlock.reactor.ReactorDistributedLockConnector;
import com.mongodb.MongoCommandException;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.mongo.MongoDistributedLockQueries.*;

class ReactorMongoDistributedLockConnector implements ReactorDistributedLockConnector {
    private static final int DUPLICATE_KEY_ERROR_CODE = 11000;
    private static final FindOneAndReplaceOptions upsertOptions = new FindOneAndReplaceOptions()
            .upsert(true)
            .returnDocument(ReturnDocument.AFTER);
    private final ReactorMongoCollectionInitializer collectionInitializer;
    private final Clock clock;

    ReactorMongoDistributedLockConnector(MongoCollection<Document> collection, Clock clock) {
        expectNonNull(collection, "collection");
        this.collectionInitializer = new ReactorMongoCollectionInitializer(collection);
        this.clock = expectNonNull(clock, "clock");
    }

    @Override
    @NotNull
    public Mono<InitializationResult> initialize() {
        return collectionInitializer.getInitializedCollection()
                .map(collection -> InitializationResult.of(true))
                .onErrorMap(e -> new SherlockException("Could not initialize Mongo collection", e));
    }

    @Override
    @NotNull
    public Mono<AcquireResult> acquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        Instant now = now();
        return upsert(
                queryReleased(lockRequest.getLockId(), now),
                MongoDistributedLock.fromLockRequest(lockRequest, now)
        )
                .map(AcquireResult::of)
                .onErrorMap(e -> new SherlockException("Could not acquire lock: " + lockRequest, e));
    }

    @Override
    @NotNull
    public Mono<AcquireResult> acquireOrProlong(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        Instant now = now();
        return upsert(
                queryAcquiredOrReleased(lockRequest.getLockId(), lockRequest.getOwnerId(), now),
                MongoDistributedLock.fromLockRequest(lockRequest, now)
        )
                .map(AcquireResult::of)
                .onErrorMap(e -> new SherlockException("Could not acquire or prolong lock: " + lockRequest, e));
    }

    @Override
    @NotNull
    public Mono<AcquireResult> forceAcquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        return upsert(
                queryById(lockRequest.getLockId()),
                MongoDistributedLock.fromLockRequest(lockRequest, now())
        )
                .map(AcquireResult::of)
                .onErrorMap(e -> new SherlockException("Could not force acquire lock: " + lockRequest, e));
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> release(@NotNull LockId lockId, @NotNull OwnerId ownerId) {
        expectNonNull(lockId, "lockId");
        expectNonNull(ownerId, "ownerId");
        return delete(queryAcquired(lockId, ownerId))
                .map(ReleaseResult::of)
                .onErrorMap(e -> new SherlockException("Could not release lock: " + lockId.getValue() + ", owner: " + ownerId, e));
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> forceRelease(@NotNull LockId lockId) {
        expectNonNull(lockId, "lockId");
        return delete(queryById(lockId))
                .map(ReleaseResult::of)
                .onErrorMap(e -> new SherlockException("Could not force release lock: " + lockId.getValue(), e));
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> forceReleaseAll() {
        return deleteAll()
                .map(ReleaseResult::of)
                .onErrorMap(e -> new SherlockException("Could not force release all locks", e));
    }

    private Mono<Boolean> deleteAll() {
        return getLockCollection()
                .map(collection -> collection.deleteMany(new BsonDocument()))
                .flatMap(Mono::from)
                .map(result -> result.getDeletedCount() > 0)
                .defaultIfEmpty(false);
    }

    private Mono<Boolean> delete(Bson query) {
        return getLockCollection()
                .map(collection -> collection.findOneAndDelete(query))
                .flatMap(Mono::from)
                .map(MongoDistributedLock::fromDocument)
                .map(lock -> lock.isActive(now()))
                .defaultIfEmpty(false);
    }

    private Mono<Boolean> upsert(Bson query, MongoDistributedLock lock) {
        return getLockCollection()
                .map(collection -> collection.findOneAndReplace(query, lock.toDocument(), upsertOptions))
                .flatMap(Mono::from)
                .map(document -> document != null
                        && MongoDistributedLock.fromDocument(document).equals(lock))
                .onErrorResume(MongoCommandException.class, exception ->
                        exception.getErrorCode() == DUPLICATE_KEY_ERROR_CODE
                                ? Mono.just(false)
                                : Mono.error(exception)
                );
    }

    private Instant now() {
        return clock.instant();
    }

    private Mono<MongoCollection<Document>> getLockCollection() {
        return collectionInitializer.getInitializedCollection();
    }
}
