package com.coditory.sherlock.samples.mongo.rxjava;

import com.coditory.sherlock.migrator.ChangeSet;
import com.coditory.sherlock.mongo.rxjava.MongoSherlock;
import com.coditory.sherlock.rxjava.Sherlock;
import com.coditory.sherlock.rxjava.migrator.SherlockMigrator;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.reactivex.rxjava3.core.Completable;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoRxAnnotatedMigrationSample {
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
            .blockingGet();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
            .addAnnotatedChangeSets(new AnnotatedMigration2())
            .migrate()
            .blockingGet();
    }

    public static class AnnotatedMigration {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @ChangeSet(order = 0, id = "change-set-a")
        public Completable changeSetA() {
            return Completable.fromRunnable(() -> logger.info("Annotated change-set: A"));
        }

        @ChangeSet(order = 1, id = "change-set-b")
        public Completable changeSetB() {
            return Completable.fromRunnable(() -> logger.info("Annotated change-set: B"));
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
