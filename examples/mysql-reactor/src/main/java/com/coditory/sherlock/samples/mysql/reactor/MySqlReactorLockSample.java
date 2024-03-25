package com.coditory.sherlock.samples.mysql.reactor;

import com.coditory.sherlock.reactor.DistributedLock;
import com.coditory.sherlock.reactor.Sherlock;
import com.coditory.sherlock.sql.BindingMapper;
import com.coditory.sherlock.sql.reactor.SqlSherlock;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlReactorLockSample {
    private static final Logger logger = LoggerFactory.getLogger(MySqlReactorLockSample.class);

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
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock
            .runLocked(() -> logger.info("Lock acquired!"))
            .block();
    }
}
