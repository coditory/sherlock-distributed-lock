package com.coditory.sherlock.samples.inmem.reactor;

import com.coditory.sherlock.inmem.reactor.InMemorySherlock;
import com.coditory.sherlock.reactor.Sherlock;
import com.coditory.sherlock.reactor.migrator.SherlockMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class InMemReactorMigrationSample {
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
        new InMemReactorMigrationSample().sample();
    }
}
