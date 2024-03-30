package com.coditory.sherlock.samples.mongo.reactor;

import com.coditory.sherlock.migrator.ChangeSet;
import com.coditory.sherlock.mongo.reactor.MongoSherlock;
import com.coditory.sherlock.reactor.Sherlock;
import com.coditory.sherlock.reactor.migrator.SherlockMigrator;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class MongoReactorAnnotatedMigrationSample {
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
            .addAnnotatedChangeSets(new AnnotatedMigration())
            .migrate()
            .block();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
            .addAnnotatedChangeSets(new AnnotatedMigration2())
            .migrate()
            .block();
    }

    public static class AnnotatedMigration {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @ChangeSet(order = 0, id = "change-set-a")
        public Mono<?> changeSetA() {
            return Mono.fromRunnable(() -> logger.info("Annotated change-set: A"));
        }

        @ChangeSet(order = 1, id = "change-set-b")
        public Mono<?> changeSetB() {
            return Mono.fromRunnable(() -> logger.info("Annotated change-set: B"));
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
