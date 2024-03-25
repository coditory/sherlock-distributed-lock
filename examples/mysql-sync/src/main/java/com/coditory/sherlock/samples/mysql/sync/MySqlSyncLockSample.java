package com.coditory.sherlock.samples.mysql.sync;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.sql.SqlSherlock;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class MySqlSyncLockSample {
    private static final Logger logger = LoggerFactory.getLogger(MySqlSyncLockSample.class);

    private static DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        config.setUsername("mysql");
        config.setPassword("mysql");
        return new HikariDataSource(config);
    }

    public static void main(String[] args) {
        Sherlock sherlock = SqlSherlock.create(dataSource());
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.runLocked(() -> logger.info("Lock acquired!"));
    }
}
