package com.coditory.sherlock.samples.postgres.sync;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.sql.SqlSherlock;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class PostgresSyncLockSample {
    private static final Logger logger = LoggerFactory.getLogger(PostgresSyncLockSample.class);

    private static DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
        config.setUsername("postgres");
        config.setPassword("postgres");
        return new HikariDataSource(config);
    }

    public static void main(String[] args) {
        Sherlock sherlock = SqlSherlock.create(dataSource());
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
    }
}
