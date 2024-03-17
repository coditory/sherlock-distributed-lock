package com.coditory.sherlock.mongo.coroutines

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.coroutines.KtSherlock
import com.coditory.sherlock.mongo.MongoHolder
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingKtSherlockWrapper.blockingKtSherlock
import static com.coditory.sherlock.mongo.coroutines.KtMongoSherlockBuilder.coroutineMongoSherlock

trait UsesKtMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String collectionName) {
        MongoCollection<Document> collection = KtMongoOperations.INSTANCE.getLocksCollection(
                KtMongoClientHolder.getClient(), MongoHolder.databaseName, collectionName)
        KtSherlock coroutinesLocks = coroutineMongoSherlock()
                .withLocksCollection(collection)
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingKtSherlock(coroutinesLocks)
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

