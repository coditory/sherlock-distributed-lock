package com.coditory.sherlock.samples.inmem.sync;

import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.inmem.InMemorySherlock;
import com.coditory.sherlock.migrator.ChangeSet;
import com.coditory.sherlock.migrator.SherlockMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class InMemSyncAnnotatedMigrationSample {
    private final Sherlock sherlock = InMemorySherlock.builder()
            .withClock(Clock.systemUTC())
            .withUniqueOwnerId()
            .withSharedStorage()
            .build();

    private void sample() {
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(new AnnotatedMigration())
                .migrate();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
                .addAnnotatedChangeSets(new AnnotatedMigration2())
                .migrate();
    }

    public static void main(String[] args) {
        new InMemSyncAnnotatedMigrationSample().sample();
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
