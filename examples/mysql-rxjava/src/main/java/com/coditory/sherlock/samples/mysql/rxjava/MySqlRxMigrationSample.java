package com.coditory.sherlock.samples.mysql.rxjava;

import com.coditory.sherlock.rxjava.Sherlock;
import com.coditory.sherlock.rxjava.migrator.SherlockMigrator;
import com.coditory.sherlock.sql.BindingMapper;
import com.coditory.sherlock.sql.rxjava.SqlSherlock;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlRxMigrationSample {
    private static final Logger logger = LoggerFactory.getLogger(MySqlRxMigrationSample.class);

    private static ConnectionFactory getConnectionFactory() {
        String database = "test";
        ConnectionFactoryOptions options = ConnectionFactoryOptions
            .parse("r2dbc:mysql://localhost:3306/" + database)
            .mutate()
            .option(ConnectionFactoryOptions.USER, "mysql")
            .option(ConnectionFactoryOptions.PASSWORD, "mysql")
            .option(ConnectionFactoryOptions.DATABASE, database)
            .build();
        return ConnectionFactories.get(options);
    }

    public static void main(String[] args) {
        Sherlock sherlock = SqlSherlock.create(getConnectionFactory(), BindingMapper.MYSQL_MAPPER);
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
            .addChangeSet("change-set-1", () -> logger.info("Change-set 1"))
            .addChangeSet("change-set-2", () -> logger.info("Change-set 2"))
            .migrate()
            .blockingGet();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
            .addChangeSet("change-set-1", () -> logger.info("Change-set 1"))
            .addChangeSet("change-set-2", () -> logger.info("Change-set 2"))
            .addChangeSet("change-set-3", () -> logger.info("Change-set 3"))
            .migrate()
            .blockingGet();
    }
}
