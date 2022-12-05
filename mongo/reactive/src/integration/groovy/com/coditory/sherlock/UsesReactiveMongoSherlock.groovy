package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static BlockingReactiveSherlockWrapper.blockingReactiveSherlock
import static ReactiveMongoHolder.databaseName
import static com.coditory.sherlock.ReactiveMongoSherlockBuilder.reactiveMongoSherlock

trait UsesReactiveMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    static final String locksCollectionName = "locks"

    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock) {
        ReactiveSherlock reactiveLocks = reactiveMongoSherlock()
                .withLocksCollection(getLocksCollection())
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingReactiveSherlock(reactiveLocks)
    }

    MongoCollection<Document> getLocksCollection() {
        return ReactiveMongoHolder.getClient()
                .getDatabase(databaseName)
                .getCollection(locksCollectionName)
    }

    @Override
    void stopDatabase() {
        ReactiveMongoHolder.stopDb()
    }

    @Override
    void startDatabase() {
        ReactiveMongoHolder.startDb()
    }
}
