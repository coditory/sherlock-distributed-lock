package com.coditory.sherlock;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class ReactorSqlTableInitializer {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ConnectionFactory connectionFactory;
    private final SqlLockQueries sqlQueries;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    ReactorSqlTableInitializer(ConnectionFactory connectionFactory, SqlLockQueries sqlQueries) {
        expectNonNull(connectionFactory, "connectionFactory");
        expectNonNull(sqlQueries, "sqlQueries");
        this.connectionFactory = connectionFactory;
        this.sqlQueries = sqlQueries;
    }

    Mono<Connection> getInitializedConnection() {
        return initialized.compareAndSet(false, true)
                ? initialize()
                : createConnectionWithRetry();
    }

    private Mono<Connection> initialize() {
        return createConnectionWithRetry()
                .flatMap(this::createTable)
                .flatMap(this::createIndex);
    }

    private Mono<Connection> createTable(Connection connection) {
        Statement createTableStatement = connection.createStatement(sqlQueries.createLocksTable());
        return Mono.<Result>from(createTableStatement.execute())
                .flatMap(__ -> Mono.from(connection.commitTransaction()).thenReturn(connection))
                .onErrorResume(e -> {
                    Statement checkTableStatement = connection.createStatement(sqlQueries.checkTableExits());
                    return Mono.from(checkTableStatement.execute())
                            .flatMap(r -> Mono.from(r.getRowsUpdated()).thenReturn(connection));
                })
                .onErrorMap(e -> {
                    initialized.set(false);
                    return new SherlockException("Could not initialize locks table", e);
                });
    }

    private Mono<Connection> createIndex(Connection connection) {
        Statement createIndexStatement = connection.createStatement(sqlQueries.createLocksIndex());
        return Mono.<Result>from(createIndexStatement.execute())
                .flatMap(__ -> Mono.from(connection.commitTransaction()).thenReturn(connection))
                .onErrorMap(e -> {
                    initialized.set(false);
                    return new SherlockException("Could not initialize locks table index", e);
                });
    }

    private Mono<Connection> createConnectionWithRetry() {
        // Retrying connection because of a bug in connection pool
        // https://github.com/r2dbc/r2dbc-pool/issues/164
        // it seems that retrying connection once solves the issue
        return Mono.<Connection>from(connectionFactory.create())
                .onErrorResume(e -> {
                    logger.debug("Could ne create connection. Retrying one more time", e);
                    return Mono.from(connectionFactory.create());
                });
    }
}
