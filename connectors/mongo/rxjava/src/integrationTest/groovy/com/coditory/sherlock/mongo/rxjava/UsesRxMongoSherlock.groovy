package com.coditory.sherlock.mongo.rxjava

import com.coditory.sherlock.BlockingRxSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.mongo.MongoHolder
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

trait UsesRxMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        com.coditory.sherlock.rxjava.Sherlock reactiveLocks = MongoSherlock.builder()
                .withLocksCollection(getLocksCollection(collectionName))
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return new BlockingRxSherlockWrapper(reactiveLocks)
    }

    MongoCollection<Document> getLocksCollection(String collectionName) {
        return MongoClientHolder.getClient()
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
