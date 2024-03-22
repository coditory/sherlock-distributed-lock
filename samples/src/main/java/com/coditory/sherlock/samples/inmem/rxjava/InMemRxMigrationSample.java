package com.coditory.sherlock.samples.inmem.rxjava;

import com.coditory.sherlock.inmem.rxjava.InMemorySherlock;
import com.coditory.sherlock.rxjava.Sherlock;
import com.coditory.sherlock.rxjava.migrator.SherlockMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class InMemRxMigrationSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    Sherlock sherlock = InMemorySherlock.builder()
            .withClock(Clock.systemUTC())
            .withUniqueOwnerId()
            .withSharedStorage()
            .build();

    void sample() {
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

    public static void main(String[] args) {
        new InMemRxMigrationSample().sample();
    }
}
