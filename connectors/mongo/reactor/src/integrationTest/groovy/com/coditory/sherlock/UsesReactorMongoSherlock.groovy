package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static BlockingReactorSherlockWrapper.blockingReactorSherlock
import static ReactorMongoSherlockBuilder.reactorMongoSherlock
import static com.coditory.sherlock.MongoHolder.databaseName

trait UsesReactorMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        ReactorSherlock reactorLocks = reactorMongoSherlock()
                .withLocksCollection(getLocksCollection(collectionName))
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingReactorSherlock(reactorLocks)
    }

    MongoCollection<Document> getLocksCollection(String collectionName) {
        return ReactorMongoClientHolder.getClient()
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
