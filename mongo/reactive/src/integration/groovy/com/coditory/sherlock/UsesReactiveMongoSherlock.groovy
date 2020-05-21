package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static BlockingReactiveSherlockWrapper.blockingReactiveSherlock
import static MongoHolder.databaseName
import static com.coditory.sherlock.ReactiveMongoSherlockBuilder.reactiveMongoSherlock

trait UsesReactiveMongoSherlock implements DistributedLocksCreator {
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
        return MongoHolder.getClient()
                .getDatabase(databaseName)
                .getCollection(locksCollectionName)
    }
}
