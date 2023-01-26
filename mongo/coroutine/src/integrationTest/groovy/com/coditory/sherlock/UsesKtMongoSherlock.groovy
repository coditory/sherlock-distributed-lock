package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingKtSherlockWrapper.blockingKtSherlock
import static com.coditory.sherlock.KtMongoSherlockBuilder.coroutineMongoSherlock

trait UsesKtMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    static final String locksCollectionName = "locks"

    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
        KtSherlock coroutinesLocks = coroutineMongoSherlock()
                .withLocksCollection(getLocksCollection())
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingKtSherlock(coroutinesLocks)
    }

    MongoCollection<Document> getLocksCollection() {
        return KtMongoHolder.getClient()
                .getDatabase(KtMongoHolder.databaseName)
                .getCollection(locksCollectionName)
    }

    @Override
    void stopDatabase() {
        KtMongoHolder.stopDb()
    }

    @Override
    void startDatabase() {
        KtMongoHolder.startDb()
    }
}

