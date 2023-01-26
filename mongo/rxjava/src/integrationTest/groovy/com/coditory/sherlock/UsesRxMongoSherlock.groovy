package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static BlockingRxSherlockWrapper.blockingRxSherlock
import static RxMongoHolder.databaseName
import static com.coditory.sherlock.RxMongoSherlockBuilder.rxMongoSherlock

trait UsesRxMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    static final String locksCollectionName = "locks"

    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock) {
        RxSherlock reactiveLocks = rxMongoSherlock()
                .withLocksCollection(getLocksCollection())
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingRxSherlock(reactiveLocks)
    }

    MongoCollection<Document> getLocksCollection() {
        return RxMongoHolder.getClient()
                .getDatabase(databaseName)
                .getCollection(locksCollectionName)
    }

    @Override
    void stopDatabase() {
        RxMongoHolder.stopDb()
    }

    @Override
    void startDatabase() {
        RxMongoHolder.startDb()
    }
}
