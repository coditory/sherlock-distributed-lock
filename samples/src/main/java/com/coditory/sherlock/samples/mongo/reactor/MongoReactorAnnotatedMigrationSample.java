package com.coditory.sherlock.samples.mongo.reactor;

import com.coditory.sherlock.migrator.ChangeSet;
import com.coditory.sherlock.mongo.reactor.MongoSherlock;
import com.coditory.sherlock.reactor.Sherlock;
import com.coditory.sherlock.reactor.migrator.SherlockMigrator;
import com.coditory.sherlock.samples.inmem.sync.InMemSyncAnnotatedMigrationSample;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;

public class MongoReactorAnnotatedMigrationSample {
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
                .addAnnotatedChangeSets(new InMemSyncAnnotatedMigrationSample.AnnotatedMigration())
                .migrate()
                .block();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(new InMemSyncAnnotatedMigrationSample.AnnotatedMigration2())
                .migrate()
                .block();
    }

    public static void main(String[] args) {
        new MongoReactorAnnotatedMigrationSample().sample();
    }

    public static class AnnotatedMigration {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @ChangeSet(order = 0, id = "change-set-a")
        public void changeSetA() {
            logger.info("Annotated change-set: A");
        }

        @ChangeSet(order = 1, id = "change-set-b")
        public void changeSetB() {
            logger.info("Annotated change-set: B");
        }
    }

    public static class AnnotatedMigration2 {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @ChangeSet(order = 0, id = "change-set-a")
        public void changeSetA() {
            logger.info("Annotated change-set: A");
        }

        @ChangeSet(order = 1, id = "change-set-b")
        public void changeSetB() {
            logger.info("Annotated change-set: B");
        }

        @ChangeSet(order = 2, id = "change-set-c")
        public void changeSetC() {
            logger.info("Annotated change-set: C");
        }
    }
}
