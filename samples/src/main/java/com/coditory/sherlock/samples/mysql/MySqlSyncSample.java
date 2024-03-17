package com.coditory.sherlock.samples.mysql;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.migrator.SherlockMigrator;
import com.coditory.sherlock.sql.SqlSherlock;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.Clock;
import java.time.Duration;

public class MySqlSyncSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Sherlock sherlock = SqlSherlock.builder()
            .withClock(Clock.systemUTC())
            .withLockDuration(Duration.ofMinutes(5))
            .withUniqueOwnerId()
            .withDataSource(dataSource())
            .withLocksTable("LOCKS")
            .build();


    private static DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        config.setUsername("mysql");
        config.setPassword("mysql");
        return new HikariDataSource(config);
    }

    void sampleMySqlLockUsage() throws Exception {
        logger.info(">>> SAMPLE: Lock usage");
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
    }

    private void sampleMySqlMigration() {
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
        sampleMySqlLockUsage();
        sampleMySqlMigration();
    }

    public static void main(String[] args) throws Exception {
        new MySqlSyncSample().runSamples();
    }
}
