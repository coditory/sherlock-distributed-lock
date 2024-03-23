package com.coditory.sherlock.samples.mongo.rxjava;

import com.coditory.sherlock.mongo.rxjava.MongoSherlock;
import com.coditory.sherlock.rxjava.Sherlock;
import com.coditory.sherlock.rxjava.migrator.SherlockMigrator;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoRxMigrationSample {
    private static final Logger logger = LoggerFactory.getLogger(MongoRxMigrationSample.class);

    private static MongoCollection<Document> locksCollection() {
        String database = "sherlock";
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
        return mongoClient
                .getDatabase("sherlock")
                .getCollection("locks");
    }

    public static void main(String[] args) {
        Sherlock sherlock = MongoSherlock.create(locksCollection());
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
                .addChangeSet("change-set-1", () -> logger.info("Change-set 1"))
                .addChangeSet("change-set-2", () -> logger.info("Change-set 2"))
                .migrate()
                .blockingGet();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
                .addChangeSet("change-set-1", () -> logger.info("Change-set 1"))
                .addChangeSet("change-set-2", () -> logger.info("Change-set 2"))
                .addChangeSet("change-set-3", () -> logger.info("Change-set 3"))
                .migrate()
                .blockingGet();
    }
}
