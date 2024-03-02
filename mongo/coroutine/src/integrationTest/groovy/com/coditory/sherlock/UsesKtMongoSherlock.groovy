package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingKtSherlockWrapper.blockingKtSherlock
import static com.coditory.sherlock.KtMongoSherlockBuilder.coroutineMongoSherlock
import static com.coditory.sherlock.MongoHolder.databaseName

trait UsesKtMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String collectionName) {
        KtSherlock coroutinesLocks = coroutineMongoSherlock()
                .withLocksCollection(getLocksCollection(collectionName))
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingKtSherlock(coroutinesLocks)
    }

    MongoCollection<Document> getLocksCollection(String collectionName) {
        return KtMongoClientHolder.getClient()
                .getDatabase(databaseName)
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

