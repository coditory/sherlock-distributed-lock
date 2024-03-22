package com.coditory.sherlock.samples.inmem.sync;

import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.inmem.InMemorySherlock;
import com.coditory.sherlock.migrator.SherlockMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class InMemSyncMigrationSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Sherlock sherlock = InMemorySherlock.builder()
            .withClock(Clock.systemUTC())
            .withUniqueOwnerId()
            .withSharedStorage()
            .build();

    private void sample() {
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
                .addChangeSet("change-set-1", () -> logger.info("Change-set 1"))
                .addChangeSet("change-set-2", () -> logger.info("Change-set 2"))
                .migrate();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
                .addChangeSet("change-set-1", () -> logger.info("Change-set 1"))
                .addChangeSet("change-set-2", () -> logger.info("Change-set 2"))
                .addChangeSet("change-set-3", () -> logger.info("Change-set 3"))
                .migrate();
    }

    public static void main(String[] args) {
        new InMemSyncMigrationSample().sample();
    }
}
