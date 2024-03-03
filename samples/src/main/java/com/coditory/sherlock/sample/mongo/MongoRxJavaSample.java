package com.coditory.sherlock.sample.mongo;

import com.coditory.sherlock.RxDistributedLock;
import com.coditory.sherlock.RxSherlock;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.reactivex.Single;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.RxMongoSherlockBuilder.rxMongoSherlock;

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
        RxSherlock sherlock = rxMongoSherlock()
                .withClock(Clock.systemUTC())
                .withLockDuration(Duration.ofMinutes(5))
                .withUniqueOwnerId()
                .withLocksCollection(locksCollection())
                .build();
        // ...or short equivalent:
        // RxSherlock sherlockWithDefaults = rxSherlock(reactiveMongoSherlock(locksCollection()));
        RxDistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(Single.fromCallable(() -> {
            logger.info("Lock acquired!");
            return true;
        })).blockingGet();
    }

    public static void main(String[] args) {
        new MongoRxJavaSample().sampleMongoLockUsage();
    }
}
