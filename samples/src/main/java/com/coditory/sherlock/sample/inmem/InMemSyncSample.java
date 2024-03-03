package com.coditory.sherlock.sample.inmem;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.SherlockMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

import static com.coditory.sherlock.InMemorySherlockBuilder.inMemorySherlockBuilder;

public class InMemSyncSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Sherlock sherlock = inMemorySherlockBuilder()
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
        new SherlockMigrator("db-migration", sherlock)
                .addChangeSet("change set 1", () -> logger.info(">>> Change set 1"))
                .addChangeSet("change set 2", () -> logger.info(">>> Change set 2"))
                .migrate();
        // second commit - only new change set is executed
        new SherlockMigrator("db-migration", sherlock)
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
