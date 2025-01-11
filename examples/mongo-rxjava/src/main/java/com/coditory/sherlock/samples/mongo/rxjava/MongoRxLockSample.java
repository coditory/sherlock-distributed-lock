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

public class MongoRxLockSample {
    private static final Logger logger = LoggerFactory.getLogger(MongoRxLockSample.class);

    private static MongoCollection<Document> getCollection() {
        String database = "sherlock";
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
        return mongoClient
            .getDatabase("sherlock")
            .getCollection("locks");
    }

    public static void main(String[] args) {
        Sherlock sherlock = MongoSherlock.create(getCollection());
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.runLocked(() -> logger.info("Lock acquired!"))
            .blockingGet();
    }
}
