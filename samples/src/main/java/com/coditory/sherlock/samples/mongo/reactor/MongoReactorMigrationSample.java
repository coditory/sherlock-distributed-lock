package com.coditory.sherlock.samples.mongo.reactor;

import com.coditory.sherlock.mongo.reactor.MongoSherlock;
import com.coditory.sherlock.reactor.Sherlock;
import com.coditory.sherlock.reactor.migrator.SherlockMigrator;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;

public class MongoReactorMigrationSample {
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

    private void sample() {
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
                .addChangeSet("change-set-1", () -> logger.info("Change-set 1"))
                .addChangeSet("change-set-2", () -> logger.info("Change-set 2"))
                .migrate()
                .block();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
                .addChangeSet("change-set-1", () -> logger.info("Change-set 1"))
                .addChangeSet("change-set-2", () -> logger.info("Change-set 2"))
                .addChangeSet("change-set-3", () -> logger.info("Change-set 3"))
                .migrate()
                .block();
    }

    public static void main(String[] args) {
        new MongoReactorMigrationSample().sample();
    }
}
