package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.mongo.MongoHolder
import com.coditory.sherlock.reactor.ReactorSherlock
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static BlockingReactorSherlockWrapper.blockingReactorSherlock
import static com.coditory.sherlock.mongo.MongoHolder.databaseName
import static com.coditory.sherlock.mongo.reactor.ReactorMongoSherlockBuilder.reactorMongoSherlock

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
