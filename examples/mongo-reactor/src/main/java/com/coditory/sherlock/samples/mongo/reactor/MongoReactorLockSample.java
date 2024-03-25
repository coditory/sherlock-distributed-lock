package com.coditory.sherlock.samples.mongo.reactor;

import com.coditory.sherlock.mongo.reactor.MongoSherlock;
import com.coditory.sherlock.reactor.DistributedLock;
import com.coditory.sherlock.reactor.Sherlock;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoReactorLockSample {
    private static final Logger logger = LoggerFactory.getLogger(MongoReactorLockSample.class);

    private static MongoCollection<Document> getCollection() {
        String database = "sherlock";
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
        return mongoClient
            .getDatabase("sherlock")
            .getCollection("locks");
    }

    public static void main(String[] args) {
        Sherlock sherlock = MongoSherlock.create(getCollection());
        DistributedLock lock = sherlock.createLock("sample-lock2");
        lock.runLocked(() -> logger.info("Lock acquired!"))
            .block();
    }
}
