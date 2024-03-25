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

public class MongoSyncLockSample {
    private static final Logger logger = LoggerFactory.getLogger(MongoSyncLockSample.class);

    private static MongoCollection<Document> getCollection() {
        String database = "sherlock";
        String connectionString = "mongodb://localhost:27017/" + database;
        MongoClient mongoClient = MongoClients.create(connectionString);
        return mongoClient
            .getDatabase("sherlock")
            .getCollection("locks");
    }

    public static void main(String[] args) {
        Sherlock sherlock = MongoSherlock.create(getCollection());
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.runLocked(() -> logger.info("Lock acquired!"));
    }
}
