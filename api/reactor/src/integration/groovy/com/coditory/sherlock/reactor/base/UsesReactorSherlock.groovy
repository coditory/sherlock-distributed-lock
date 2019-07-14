package com.coditory.sherlock.reactor.base

import com.coditory.sherlock.reactive.ReactiveMongoSherlock
import com.coditory.sherlock.reactor.ReactorSherlock
import com.coditory.sherlock.tests.base.DistributedLocksCreator
import com.coditory.sherlock.tests.base.TestableDistributedLocks
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.BsonDocument
import org.bson.Document
import org.junit.After
import reactor.core.publisher.Flux

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.reactor.base.BlockingReactorSherlockWrapper.blockReactorSherlock

trait UsesReactorSherlock implements DistributedLocksCreator {
  static final String locksCollectionName = "sherlock"

  @Override
  TestableDistributedLocks createDistributedLocks(String ownerId, Duration duration, Clock clock) {
    ReactorSherlock reactorSherlock = ReactiveMongoSherlock.builder()
        .withMongoClient(MongoInitializer.mongoClient)
        .withDatabaseName(MongoInitializer.databaseName)
        .withCollectionName(locksCollectionName)
        .withOwnerId(ownerId)
        .withLockDuration(duration)
        .withClock(clock)
        .build { ReactorSherlock.wrapReactiveSherlock(it) }
    return blockReactorSherlock(reactorSherlock)
  }

  @After
  void clearLockCollection() {
    Flux.from(getLocksCollection().deleteMany(new BsonDocument()))
        .blockLast()
  }

  MongoCollection<Document> getLocksCollection() {
    return MongoInitializer.mongoClient.getDatabase(MongoInitializer.databaseName)
        .getCollection(locksCollectionName)
  }
}
