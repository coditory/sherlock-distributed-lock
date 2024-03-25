package com.coditory.sherlock.mongo.coroutines

import com.coditory.sherlock.BlockingKtSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.mongo.MongoHolder
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

trait UsesKtMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String collectionName) {
        MongoCollection<Document> collection = MongoOperations.INSTANCE.getLocksCollection(
            MongoClientHolder.getClient(), MongoHolder.databaseName, collectionName)
        com.coditory.sherlock.coroutines.Sherlock coroutinesLocks = MongoSherlock.builder()
            .withLocksCollection(collection)
            .withOwnerId(instanceId)
            .withLockDuration(duration)
            .withClock(clock)
            .build()
        return new BlockingKtSherlockWrapper(coroutinesLocks)
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

