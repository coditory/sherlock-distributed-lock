package com.coditory.sherlock.samples.postgres;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.SherlockMigrator;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.sql.SqlSherlockBuilder.sqlSherlock;

public class PostgresSyncSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Sherlock sherlock = sqlSherlock()
            .withClock(Clock.systemUTC())
            .withLockDuration(Duration.ofMinutes(5))
            .withUniqueOwnerId()
            .withDataSource(dataSource())
            .withLocksTable("LOCKS")
            .build();

    private static DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
        config.setUsername("postgres");
        config.setPassword("postgres");
        return new HikariDataSource(config);
    }

    void samplePostgresLockUsage() throws Exception {
        logger.info(">>> SAMPLE: Lock usage");
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
    }

    private void samplePostgresMigration() {
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

    void runSamples() throws Exception {
        samplePostgresLockUsage();
        samplePostgresMigration();
    }

    public static void main(String[] args) throws Exception {
        new PostgresSyncSample().runSamples();
    }
}
