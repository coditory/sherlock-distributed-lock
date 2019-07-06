package com.coditory.distributed.lock.mongo.reactive;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;
import com.coditory.distributed.lock.common.LockRequest;
import com.coditory.distributed.lock.reactive.driver.InitializationResult;
import com.coditory.distributed.lock.reactive.driver.LockResult;
import com.coditory.distributed.lock.reactive.driver.ReactiveDistributedLockDriver;
import com.coditory.distributed.lock.reactive.driver.UnlockResult;
import com.mongodb.MongoCommandException;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.distributed.lock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.ACQUIRED_AT_FIELD;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.ACQUIRED_BY_FIELD;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.EXPIRES_AT_FIELD;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.LOCK_ID_FIELD;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;
import static reactor.adapter.JdkFlowAdapter.publisherToFlowPublisher;

public class ReactiveMongoDistributedLockDriver implements ReactiveDistributedLockDriver {
  private static final int DUPLICATE_KEY_ERROR_CODE = 11000;
  private static final FindOneAndReplaceOptions upsertOptions = new FindOneAndReplaceOptions()
      .upsert(true)
      .returnDocument(ReturnDocument.AFTER);
  private final MongoClient mongoClient;
  private final String databaseName;
  private final String collectionName;
  private final Clock clock;
  private final AtomicBoolean indexesCreated = new AtomicBoolean();

  public ReactiveMongoDistributedLockDriver(
      MongoClient client, String databaseName, String collectionName, Clock clock) {
    this.mongoClient = expectNonNull(client, "Expected non null mongoClient");
    this.databaseName = expectNonEmpty(databaseName, "Expected non empty databaseName");
    this.collectionName = expectNonEmpty(collectionName, "Expected non empty collectionName");
    this.clock = expectNonNull(clock, "Expected non null clock");
  }

  @Override
  public Publisher<InitializationResult> initialize() {
    return publisherToFlowPublisher(createIndexes().map(InitializationResult::new));
  }

  @Override
  public Publisher<LockResult> lock(LockRequest lockRequest) {
    Instant now = now();
    return publisherToFlowPublisher(upsert(
        queryAcquiredAndReleased(lockRequest.getLockId(), lockRequest.getInstanceId(), now),
        MongoDistributedLock.fromLockRequest(lockRequest, now)
    ).map(LockResult::new));
  }

  @Override
  public Publisher<LockResult> lockOrRelock(LockRequest lockRequest) {
    Instant now = now();
    return publisherToFlowPublisher(upsert(
        queryAcquiredOrReleased(lockRequest.getLockId(), lockRequest.getInstanceId(), now),
        MongoDistributedLock.fromLockRequest(lockRequest, now)
    ).map(LockResult::new));
  }

  @Override
  public Publisher<LockResult> forceLock(LockRequest lockRequest) {
    return publisherToFlowPublisher(upsert(
        queryAcquired(lockRequest.getLockId()),
        MongoDistributedLock.fromLockRequest(lockRequest, now())
    ).map(LockResult::new));
  }

  @Override
  public Publisher<UnlockResult> unlock(LockId lockId, InstanceId instanceId) {
    return publisherToFlowPublisher(delete(queryAcquired(lockId, instanceId))
        .map(UnlockResult::new));
  }

  @Override
  public Publisher<UnlockResult> forceUnlock(LockId lockId) {
    return publisherToFlowPublisher(delete(queryAcquired(lockId))
        .map(UnlockResult::new));
  }

  @Override
  public Publisher<UnlockResult> forceUnlockAll() {
    return publisherToFlowPublisher(deleteAll()
        .map(UnlockResult::new));
  }

  private Mono<Boolean> delete(Bson query) {
    return getLockCollection()
        .map(collection -> collection.findOneAndDelete(query))
        .flatMap(Mono::from)
        .map(result -> true)
        .defaultIfEmpty(false);
  }

  private Mono<Boolean> deleteAll() {
    return getLockCollection()
        .map(collection -> collection.deleteMany(new BsonDocument()))
        .flatMap(Mono::from)
        .map(result -> result.getDeletedCount() > 0);
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

  private Mono<Boolean> createIndexes() {
    boolean shouldCreateIndexes = indexesCreated.compareAndSet(false, true);
    if (!shouldCreateIndexes) {
      return Mono.just(false);
    }
    return Mono.fromCallable(() ->
        mongoClient
            .getDatabase(databaseName)
            .getCollection(collectionName))
        .map(collection ->
            collection.createIndex(
                Indexes.ascending(LOCK_ID_FIELD, ACQUIRED_BY_FIELD, ACQUIRED_AT_FIELD),
                new IndexOptions().background(true))
        )
        .map(Mono::from).map(result -> true);
  }

  private Bson queryAcquiredAndReleased(LockId lockId, InstanceId instanceId, Instant now) {
    return and(
        eq(LOCK_ID_FIELD, lockId.getValue()),
        eq(ACQUIRED_BY_FIELD, instanceId.getValue()),
        lte(EXPIRES_AT_FIELD, now)
    );
  }

  private Bson queryAcquired(LockId lockId, InstanceId instanceId) {
    return and(
        eq(LOCK_ID_FIELD, lockId.getValue()),
        eq(ACQUIRED_BY_FIELD, instanceId.getValue())
    );
  }

  private Bson queryAcquiredOrReleased(
      LockId lockId, InstanceId instanceId, Instant now) {
    return and(
        eq(LOCK_ID_FIELD, lockId.getValue()),
        or(
            eq(ACQUIRED_BY_FIELD, instanceId.getValue()),
            lte(EXPIRES_AT_FIELD, now)
        )
    );
  }

  private Bson queryAcquired(LockId lockId) {
    return eq(LOCK_ID_FIELD, lockId.getValue());
  }

  private Instant now() {
    return clock.instant();
  }

  private Mono<MongoCollection<Document>> getLockCollection() {
    return createIndexes()
        .map(it -> mongoClient.getDatabase(databaseName))
        .map(db -> db.getCollection(collectionName));
  }
}
