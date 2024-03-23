package com.coditory.sherlock.samples.postgres.rxjava;

import com.coditory.sherlock.OwnerIdPolicy;
import com.coditory.sherlock.rxjava.DistributedLock;
import com.coditory.sherlock.rxjava.Sherlock;
import com.coditory.sherlock.sql.BindingMapper;
import com.coditory.sherlock.sql.rxjava.SqlSherlock;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.sql.BindingMapper.POSTGRES_MAPPER;

public class PostgresRxLockSample {
    private static final Logger logger = LoggerFactory.getLogger(PostgresRxLockSample.class);

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
        SqlSherlock.builder()
                .withClock(Clock.systemUTC())
                .withLockDuration(Duration.ofMinutes(5))
                .withOwnerIdPolicy(OwnerIdPolicy.uniqueOwnerId())
                .withBindingMapper(BindingMapper.POSTGRES_MAPPER)
                .withConnectionFactory(getConnectionFactory())
                .withLocksTable("LOCKS")
                .build();
        Sherlock sherlock = SqlSherlock.create(getConnectionFactory(), POSTGRES_MAPPER);
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(() -> logger.info("Lock acquired!"))
                .blockingGet();
    }
}
