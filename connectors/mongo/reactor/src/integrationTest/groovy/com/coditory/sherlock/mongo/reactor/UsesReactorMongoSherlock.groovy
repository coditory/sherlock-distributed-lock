package com.coditory.sherlock.mongo.reactor

import com.coditory.sherlock.BlockingReactorSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.mongo.MongoHolder
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.mongo.MongoHolder.databaseName

trait UsesReactorMongoSherlock implements DistributedLocksCreator, DatabaseManager {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        com.coditory.sherlock.reactor.Sherlock reactorLocks = MongoSherlock.builder()
                .withLocksCollection(getLocksCollection(collectionName))
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return new BlockingReactorSherlockWrapper(reactorLocks)
    }

    MongoCollection<Document> getLocksCollection(String collectionName) {
        return MongoClientHolder.getClient()
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
