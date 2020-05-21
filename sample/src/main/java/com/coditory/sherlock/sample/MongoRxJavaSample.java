package com.coditory.sherlock.sample;

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

import static com.coditory.sherlock.ReactiveMongoSherlockBuilder.reactiveMongoSherlock;

public class MongoRxJavaSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MongoCollection<Document> locksCollection() {
        String database = "sherlock";
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
        return mongoClient
                .getDatabase("sherlock")
                .getCollection("locks");
    }

    void sampleMongoSherlock() {
        RxSherlock sherlock = reactiveMongoSherlock()
                .withClock(Clock.systemDefaultZone())
                .withLockDuration(Duration.ofMinutes(5))
                .withUniqueOwnerId()
                .withLocksCollection(locksCollection())
                .buildWithApi(RxSherlock::rxSherlock);
        // ...or simply
        // RxSherlock sherlockWithDefaults = rxSherlock(reactiveMongoSherlock(locksCollection()));
        RxDistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(Single.fromCallable(() -> {
            logger.info("Lock acquired!");
            return true;
        })).blockingGet();
    }

    public static void main(String[] args) {
        new MongoRxJavaSample().sampleMongoSherlock();
    }
}
