package com.coditory.sherlock.samples.mysql.sync;

import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.migrator.ChangeSet;
import com.coditory.sherlock.migrator.SherlockMigrator;
import com.coditory.sherlock.sql.SqlSherlock;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.Clock;
import java.time.Duration;

public class MySqlSyncAnnotatedMigrationSample {
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
        new MySqlSyncAnnotatedMigrationSample().sample();
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
