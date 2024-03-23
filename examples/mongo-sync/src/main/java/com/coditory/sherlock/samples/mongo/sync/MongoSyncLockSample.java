package com.coditory.sherlock.samples.mongo.sync;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.mongo.MongoSherlock;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;

public class MongoSyncLockSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Sherlock sherlock = MongoSherlock.builder()
            .withClock(Clock.systemUTC())
            .withLockDuration(Duration.ofMinutes(5))
            .withUniqueOwnerId()
            .withLocksCollection(locksCollection())
            .build();

    private static MongoCollection<Document> locksCollection() {
        String database = "sherlock";
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
        return mongoClient
                .getDatabase("sherlock")
                .getCollection("locks");
    }

    void sample() {
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
    }

    public static void main(String[] args) {
        new MongoSyncLockSample().sample();
    }
}
