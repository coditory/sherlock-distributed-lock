package com.coditory.sherlock.samples.postgres.rxjava;

import com.coditory.sherlock.rxjava.Sherlock;
import com.coditory.sherlock.rxjava.migrator.SherlockMigrator;
import com.coditory.sherlock.sql.BindingMapper;
import com.coditory.sherlock.sql.rxjava.SqlSherlock;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.reactivex.rxjava3.core.Completable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresRxMigrationSample {
    private static final Logger logger = LoggerFactory.getLogger(PostgresRxMigrationSample.class);

    private static ConnectionFactory getConnectionFactory() {
        String database = "test";
        ConnectionFactoryOptions options = ConnectionFactoryOptions
            .parse("r2dbc:postgresql://localhost:5432/" + database)
            .mutate()
            .option(ConnectionFactoryOptions.USER, "postgres")
            .option(ConnectionFactoryOptions.PASSWORD, "postgres")
            .option(ConnectionFactoryOptions.DATABASE, database)
            .build();
        return ConnectionFactories.get(options);
    }

    public static void main(String[] args) {
        Sherlock sherlock = SqlSherlock.create(getConnectionFactory(), BindingMapper.POSTGRES_MAPPER);
        // first commit - all migrations are executed
        // acceptable changesets types: () -> {}, Completable, () -> Completable
        SherlockMigrator.builder(sherlock)
            .addChangeSet("change-set-1", Completable.fromRunnable(() -> logger.info("Change-set 1")))
            .addChangeSet("change-set-2", () -> Completable.fromRunnable(() -> logger.info("Change-set 2")))
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
