package com.coditory.sherlock.samples.mongo.rxjava;

import com.coditory.sherlock.mongo.rxjava.MongoSherlock;
import com.coditory.sherlock.rxjava.DistributedLock;
import com.coditory.sherlock.rxjava.Sherlock;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;

public class MongoRxLockSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Sherlock sherlock = MongoSherlock.builder()
            .withClock(Clock.systemUTC())
            .withLockDuration(Duration.ofMinutes(5))
            .withUniqueOwnerId()
            .withLocksCollection(locksCollection())
            .build();

    private MongoCollection<Document> locksCollection() {
        String database = "sherlock";
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
        return mongoClient
                .getDatabase("sherlock")
                .getCollection("locks");
    }

    void sample() {
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock
                .acquireAndExecute(() -> logger.info("Lock acquired!"))
                .blockingGet();
    }

    public static void main(String[] args) {
        new MongoRxLockSample().sample();
    }
}
