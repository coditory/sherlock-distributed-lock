package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static BlockingRxSherlockWrapper.blockingRxSherlock
import static com.coditory.sherlock.RxMongoSherlockBuilder.rxMongoSherlock

trait UsesRxMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        RxSherlock reactiveLocks = rxMongoSherlock()
                .withLocksCollection(getLocksCollection(collectionName))
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingRxSherlock(reactiveLocks)
    }

    MongoCollection<Document> getLocksCollection(String collectionName) {
        return RxMongoClientHolder.getClient()
                .getDatabase(MongoHolder.databaseName)
                .getCollection(collectionName)
    }

    @Override
    void stopDatabase() {
        MongoHolder.stopDb()
    }

    @Override
    void startDatabase() {
        MongoHolder.startDb()
    }
}
