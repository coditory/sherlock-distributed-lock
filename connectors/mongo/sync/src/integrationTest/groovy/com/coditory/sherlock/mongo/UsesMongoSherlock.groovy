package com.coditory.sherlock.mongo


import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

trait UsesMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String collectionName) {
        return MongoSherlock.builder()
            .withLocksCollection(getLocksCollection(collectionName))
            .withOwnerId(instanceId)
            .withLockDuration(duration)
            .withClock(clock)
            .build()
    }

    MongoCollection<Document> getLocksCollection(String collectionName) {
        return MongoHolder.getClient()
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

