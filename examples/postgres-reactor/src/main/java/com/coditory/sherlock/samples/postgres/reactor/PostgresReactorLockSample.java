package com.coditory.sherlock.samples.postgres.reactor;

import com.coditory.sherlock.reactor.DistributedLock;
import com.coditory.sherlock.reactor.Sherlock;
import com.coditory.sherlock.sql.BindingMapper;
import com.coditory.sherlock.sql.reactor.SqlSherlock;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;

public class PostgresReactorLockSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Sherlock sherlock = SqlSherlock.builder()
            .withClock(Clock.systemUTC())
            .withLockDuration(Duration.ofMinutes(5))
            .withUniqueOwnerId()
            .withConnectionFactory(getConnectionFactory())
            .withBindingMapper(BindingMapper.POSTGRES_MAPPER)
            .withLocksTable("LOCKS")
            .build();

    private ConnectionFactory getConnectionFactory() {
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

    void sample() {
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock
                .acquireAndExecute(() -> logger.info("Lock acquired!"))
                .block();
    }

    public static void main(String[] args) {
        new PostgresReactorLockSample().sample();
    }
}
