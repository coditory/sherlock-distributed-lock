package com.coditory.sherlock.samples.inmem.rxjava;

import com.coditory.sherlock.inmem.rxjava.InMemorySherlock;
import com.coditory.sherlock.migrator.ChangeSet;
import com.coditory.sherlock.rxjava.Sherlock;
import com.coditory.sherlock.rxjava.migrator.SherlockMigrator;
import com.coditory.sherlock.samples.inmem.reactor.InMemReactorAnnotatedMigrationSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class InMemRxAnnotatedMigrationSample {
    private final Sherlock sherlock = InMemorySherlock.builder()
            .withClock(Clock.systemUTC())
            .withUniqueOwnerId()
            .withSharedStorage()
            .build();

    private void sample() {
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(new InMemReactorAnnotatedMigrationSample.AnnotatedMigration())
                .migrate()
                .blockingGet();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(new InMemReactorAnnotatedMigrationSample.AnnotatedMigration2())
                .migrate()
                .blockingGet();
    }

    public static void main(String[] args) {
        new InMemRxAnnotatedMigrationSample().sample();
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
