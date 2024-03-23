package com.coditory.sherlock.samples.postgres.sync;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.sql.SqlSherlock;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.Clock;
import java.time.Duration;

public class PostgresSyncLockSample {
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
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
        config.setUsername("postgres");
        config.setPassword("postgres");
        return new HikariDataSource(config);
    }

    void sample() {
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
    }

    public static void main(String[] args) {
        new PostgresSyncLockSample().sample();
    }
}
