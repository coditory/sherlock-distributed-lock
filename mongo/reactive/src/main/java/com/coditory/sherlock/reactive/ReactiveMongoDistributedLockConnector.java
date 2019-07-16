package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.LockRequest;
import com.coditory.sherlock.common.MongoDistributedLock;
import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.reactive.connector.AcquireResult;
import com.coditory.sherlock.reactive.connector.InitializationResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import com.mongodb.MongoCommandException;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.Flow.Publisher;

import static com.coditory.sherlock.common.MongoDistributedLockQueries.queryAcquired;
import static com.coditory.sherlock.common.MongoDistributedLockQueries.queryAcquiredAndReleased;
import static com.coditory.sherlock.common.MongoDistributedLockQueries.queryAcquiredOrReleased;
import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;
import static reactor.adapter.JdkFlowAdapter.publisherToFlowPublisher;

class ReactiveMongoDistributedLockConnector implements ReactiveDistributedLockConnector {
  private static final int DUPLICATE_KEY_ERROR_CODE = 11000;
  private static final FindOneAndReplaceOptions upsertOptions = new FindOneAndReplaceOptions()
      .upsert(true)
      .returnDocument(ReturnDocument.AFTER);
  private final ReactiveMongoCollectionInitializer collectionInitializer;
  private final Clock clock;

  ReactiveMongoDistributedLockConnector(MongoCollection<Document> collection, Clock clock) {
    expectNonNull(collection, "Expected non null collection");
    this.collectionInitializer = new ReactiveMongoCollectionInitializer(collection);
    this.clock = expectNonNull(clock, "Expected non null clock");
  }

  @Override
  public Publisher<InitializationResult> initialize() {
    return publisherToFlowPublisher(
        collectionInitializer.getInitializedCollection()
            .map(collection -> InitializationResult.of(true)));
  }

  @Override
  public Publisher<AcquireResult> acquire(LockRequest lockRequest) {
    Instant now = now();
    return publisherToFlowPublisher(upsert(
        queryAcquiredAndReleased(lockRequest.getLockId(), lockRequest.getOwnerId(), now),
        MongoDistributedLock.fromLockRequest(lockRequest, now)
    ).map(AcquireResult::of));
  }

  @Override
  public Publisher<AcquireResult> acquireOrProlong(LockRequest lockRequest) {
    Instant now = now();
    return publisherToFlowPublisher(upsert(
        queryAcquiredOrReleased(lockRequest.getLockId(), lockRequest.getOwnerId(), now),
        MongoDistributedLock.fromLockRequest(lockRequest, now)
    ).map(AcquireResult::of));
  }

  @Override
  public Publisher<AcquireResult> forceAcquire(LockRequest lockRequest) {
    return publisherToFlowPublisher(upsert(
        queryAcquired(lockRequest.getLockId()),
        MongoDistributedLock.fromLockRequest(lockRequest, now())
    ).map(AcquireResult::of));
  }

  @Override
  public Publisher<ReleaseResult> release(LockId lockId, OwnerId ownerId) {
    return publisherToFlowPublisher(delete(queryAcquired(lockId, ownerId))
        .map(ReleaseResult::of));
  }

  @Override
  public Publisher<ReleaseResult> forceRelease(LockId lockId) {
    return publisherToFlowPublisher(delete(queryAcquired(lockId))
        .map(ReleaseResult::of));
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
