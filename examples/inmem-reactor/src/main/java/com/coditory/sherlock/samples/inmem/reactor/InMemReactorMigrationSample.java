package com.coditory.sherlock.samples.inmem.reactor;

import com.coditory.sherlock.inmem.reactor.InMemorySherlock;
import com.coditory.sherlock.reactor.Sherlock;
import com.coditory.sherlock.reactor.migrator.SherlockMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemReactorMigrationSample {
    private static final Logger logger = LoggerFactory.getLogger(InMemReactorMigrationSample.class);

    public static void main(String[] args) {
        Sherlock sherlock = InMemorySherlock.create();
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
}
