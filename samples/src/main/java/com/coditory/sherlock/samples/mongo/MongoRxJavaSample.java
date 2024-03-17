package com.coditory.sherlock.samples.mongo;

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

public class MongoRxJavaSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MongoCollection<Document> locksCollection() {
        String database = "sherlock";
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
        return mongoClient
                .getDatabase("sherlock")
                .getCollection("locks");
    }

    void sampleMongoLockUsage() {
        Sherlock sherlock = MongoSherlock.builder()
                .withClock(Clock.systemUTC())
                .withLockDuration(Duration.ofMinutes(5))
                .withUniqueOwnerId()
                .withLocksCollection(locksCollection())
                .build();
        // ...or short equivalent:
        // RxSherlock sherlockWithDefaults = rxSherlock(reactiveMongoSherlock(locksCollection()));
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(() -> {
            logger.info("Lock acquired!");
            return true;
        }).blockingGet();
    }

    public static void main(String[] args) {
        new MongoRxJavaSample().sampleMongoLockUsage();
    }
}
