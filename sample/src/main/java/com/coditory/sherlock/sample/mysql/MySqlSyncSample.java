package com.coditory.sherlock.sample.mysql;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.SherlockMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.util.Properties;

import static com.coditory.sherlock.SqlSherlockBuilder.sqlSherlock;

public class MySqlSyncSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Sherlock sherlock = sqlSherlock()
            .withClock(Clock.systemDefaultZone())
            .withLockDuration(Duration.ofMinutes(5))
            .withUniqueOwnerId()
            .withConnection(connect())
            .withLocksTable("LOCKS")
            .build();

    private static Connection connect() {
        Properties connectionProps = new Properties();
        connectionProps.put("user", "mysql");
        connectionProps.put("password", "mysql");
        try {
            return DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/test", connectionProps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not create MySQL connection", e);
        }
    }

    void sampleMySqlLockUsage() {
        logger.info(">>> SAMPLE: Lock usage");
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
    }

    private void sampleMySqlMigration() {
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
        sampleMySqlLockUsage();
        sampleMySqlMigration();
    }

    public static void main(String[] args) {
        new MySqlSyncSample().runSamples();
    }
}
