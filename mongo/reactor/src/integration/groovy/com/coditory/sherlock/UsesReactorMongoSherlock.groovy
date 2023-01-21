package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static BlockingReactorSherlockWrapper.blockingReactorSherlock
import static ReactorMongoHolder.databaseName
import static ReactorMongoSherlockBuilder.reactorMongoSherlock

trait UsesReactorMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    static final String locksCollectionName = "locks"

    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock) {
        ReactorSherlock reactorLocks = reactorMongoSherlock()
                .withLocksCollection(getLocksCollection())
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingReactorSherlock(reactorLocks)
    }

    MongoCollection<Document> getLocksCollection() {
        return ReactorMongoHolder.getClient()
                .getDatabase(databaseName)
                .getCollection(locksCollectionName)
    }

    @Override
    void stopDatabase() {
        ReactorMongoHolder.stopDb()
    }

    @Override
    void startDatabase() {
        ReactorMongoHolder.startDb()
    }
}
