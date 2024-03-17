package com.coditory.sherlock.samples.inmem;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.inmem.InMemorySherlock;
import com.coditory.sherlock.migrator.SherlockMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class InMemSyncSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Sherlock sherlock = InMemorySherlock.builder()
            .withClock(Clock.systemUTC())
            .withUniqueOwnerId()
            .withSharedStorage()
            .build();

    private void sampleInMemLockUsage() {
        logger.info(">>> SAMPLE: Lock usage");
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
    }

    private void sampleInMemMigration() {
        logger.info(">>> SAMPLE: Migration");
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
                .setMigrationId("db-migration")
                .addChangeSet("change set 1", () -> logger.info(">>> Change set 1"))
                .addChangeSet("change set 2", () -> logger.info(">>> Change set 2"))
                .migrate();
        // second commit - only new change set is executed
        SherlockMigrator.builder(sherlock)
                .setMigrationId("db-migration")
                .addChangeSet("change set 1", () -> logger.info(">>> Change set 1"))
                .addChangeSet("change set 2", () -> logger.info(">>> Change set 2"))
                .addChangeSet("change set 3", () -> logger.info(">>> Change set 3"))
                .migrate();
    }

    void runSamples() {
        sampleInMemLockUsage();
        sampleInMemMigration();
    }

    public static void main(String[] args) {
        new InMemSyncSample().runSamples();
    }
}
