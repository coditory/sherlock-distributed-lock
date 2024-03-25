package com.coditory.sherlock.samples.postgres.reactor;

import com.coditory.sherlock.reactor.DistributedLock;
import com.coditory.sherlock.reactor.Sherlock;
import com.coditory.sherlock.sql.reactor.SqlSherlock;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.coditory.sherlock.sql.BindingMapper.POSTGRES_MAPPER;

public class PostgresReactorLockSample {
    private static final Logger logger = LoggerFactory.getLogger(PostgresReactorLockSample.class);

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
        Sherlock sherlock = SqlSherlock.create(getConnectionFactory(), POSTGRES_MAPPER);
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.runLocked(() -> logger.info("Lock acquired!"))
                .block();
    }
}
