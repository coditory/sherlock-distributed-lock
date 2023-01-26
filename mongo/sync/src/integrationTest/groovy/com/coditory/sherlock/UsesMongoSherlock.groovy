package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.MongoSherlockBuilder.mongoSherlock

trait UsesMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    static final String locksCollectionName = "locks"

    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
        return mongoSherlock()
                .withLocksCollection(getLocksCollection())
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
    }

    MongoCollection<Document> getLocksCollection() {
        return MongoHolder.getClient()
                .getDatabase(MongoHolder.databaseName)
                .getCollection(locksCollectionName)
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

