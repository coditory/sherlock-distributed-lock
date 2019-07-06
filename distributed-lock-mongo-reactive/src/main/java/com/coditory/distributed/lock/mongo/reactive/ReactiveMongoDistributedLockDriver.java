package com.coditory.distributed.lock.mongo.reactive;

import com.coditory.distributed.lock.common.InstanceId;
import com.coditory.distributed.lock.common.LockId;
import com.coditory.distributed.lock.common.LockRequest;
import com.coditory.distributed.lock.reactive.driver.InitializationResult;
import com.coditory.distributed.lock.reactive.driver.LockResult;
import com.coditory.distributed.lock.reactive.driver.ReactiveDistributedLockDriver;
import com.coditory.distributed.lock.reactive.driver.UnlockResult;
import com.mongodb.Function;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.distributed.lock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;
import static com.coditory.distributed.lock.mongo.reactive.FlowOperators.emptyPublisher;
import static com.coditory.distributed.lock.mongo.reactive.FlowOperators.flatMap;
import static com.coditory.distributed.lock.mongo.reactive.FlowOperators.map;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.ACQUIRED_AT_FIELD;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.ACQUIRED_BY_FIELD;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.EXPIRES_AT_FIELD;
import static com.coditory.distributed.lock.mongo.reactive.MongoDistributedLock.Fields.LOCK_ID_FIELD;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;
import static org.reactivestreams.FlowAdapters.toFlowPublisher;

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
    boolean shouldCreateIndexes = indexesCreated.compareAndSet(false, true);
    return shouldCreateIndexes
        ? map(createIndexes(), InitializationResult::new)
        : emptyPublisher();
  }

  @Override
  public Publisher<LockResult> lock(LockRequest lockRequest) {
    Instant now = now();
    Publisher<Boolean> publisher = upsert(
        queryAcquiredAndReleased(lockRequest.getLockId(), lockRequest.getInstanceId(), now),
        MongoDistributedLock.fromLockRequest(lockRequest, now)
    );
    return map(publisher, LockResult::new);
  }

  @Override
  public Publisher<LockResult> lockOrRelock(LockRequest lockRequest) {
    Instant now = now();
    Publisher<Boolean> publisher = upsert(
        queryAcquiredOrReleased(lockRequest.getLockId(), lockRequest.getInstanceId(), now),
        MongoDistributedLock.fromLockRequest(lockRequest, now)
    );
    return map(publisher, LockResult::new);
  }

  @Override
  public Publisher<LockResult> forceLock(LockRequest lockRequest) {
    Publisher<Boolean> publisher = upsert(
        queryAcquired(lockRequest.getLockId()),
        MongoDistributedLock.fromLockRequest(lockRequest, now())
    );
    return map(publisher, LockResult::new);
  }

  @Override
  public Publisher<UnlockResult> unlock(LockId lockId, InstanceId instanceId) {
    return map(delete(queryAcquired(lockId, instanceId)), UnlockResult::new);
  }

  @Override
  public Publisher<UnlockResult> forceUnlock(LockId lockId) {
    return map(delete(queryAcquired(lockId)), UnlockResult::new);
  }

  @Override
  public Publisher<UnlockResult> forceUnlockAll() {
    return map(deleteAll(), UnlockResult::new);
  }

  private Publisher<Boolean> delete(Bson query) {
    Publisher<Document> publisher = execute(collection ->
        collection.findOneAndDelete(query));
    return map(publisher, document -> true);
  }

  private Publisher<Boolean> deleteAll() {
    Publisher<DeleteResult> publisher = execute(collection ->
        collection.deleteMany(new BsonDocument()));
    return map(publisher, result -> result.getDeletedCount() > 0);
  }

  private Publisher<Boolean> upsert(Bson query, MongoDistributedLock lock) {
    Publisher<Document> publisher = execute(collection ->
        collection.findOneAndReplace(query, lock.toDocument(), upsertOptions));
    return map(
        publisher,
        document -> document != null && MongoDistributedLock.fromDocument(document).equals(lock));
  }

  private Publisher<Boolean> createIndexes() {
    indexesCreated.set(true);
    Publisher<String> publisher = execute(collection ->
        collection.createIndex(
            Indexes.ascending(LOCK_ID_FIELD, ACQUIRED_BY_FIELD, ACQUIRED_AT_FIELD),
            new IndexOptions().background(true)));
    return map(publisher, result -> true);
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

  private <R> Publisher<R> execute(
      Function<MongoCollection<Document>, org.reactivestreams.Publisher<? extends R>> action) {
    return flatMap(getLockCollection(), collection -> toFlowPublisher(action.apply(collection)));
  }

  private Publisher<MongoCollection<Document>> getLockCollection() {
    return map(
        initialize(),
        result -> mongoClient.getDatabase(databaseName)
            .getCollection(collectionName));
  }
}
