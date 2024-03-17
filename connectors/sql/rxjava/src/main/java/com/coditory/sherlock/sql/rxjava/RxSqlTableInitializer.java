package com.coditory.sherlock.sql.rxjava;

import com.coditory.sherlock.SherlockException;
import com.coditory.sherlock.sql.SqlLockQueries;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class RxSqlTableInitializer {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ConnectionFactory connectionFactory;
    private final SqlLockQueries sqlQueries;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    RxSqlTableInitializer(ConnectionFactory connectionFactory, SqlLockQueries sqlQueries) {
        expectNonNull(connectionFactory, "connectionFactory");
        expectNonNull(sqlQueries, "sqlQueries");
        this.connectionFactory = connectionFactory;
        this.sqlQueries = sqlQueries;
    }

    Single<Connection> getInitializedConnection() {
        return initialized.compareAndSet(false, true)
                ? initialize()
                : createConnectionWithRetry();
    }

    private Single<Connection> initialize() {
        return createConnectionWithRetry()
                .flatMap(this::createTable)
                .flatMap(this::createIndex);
    }

    private Single<Connection> createTable(Connection connection) {
        Statement createTableStatement = connection.createStatement(sqlQueries.createLocksTable());
        return Single.<Result>fromPublisher(createTableStatement.execute())
                .flatMap(__ -> commit(connection))
                .onErrorResumeNext(e -> {
                    Statement checkTableStatement = connection.createStatement(sqlQueries.checkTableExits());
                    return Single.fromPublisher(checkTableStatement.execute())
                            .flatMap(r -> Single.fromPublisher(r.getRowsUpdated()).map(__ -> connection));
                })
                .onErrorResumeNext(e -> {
                    initialized.set(false);
                    return Single.error(new SherlockException("Could not initialize locks table", e));
                });
    }

    private Single<Connection> createIndex(Connection connection) {
        Statement createIndexStatement = connection.createStatement(sqlQueries.createLocksIndex());
        return Single.<Result>fromPublisher(createIndexStatement.execute())
                .flatMap(__ -> commit(connection))
                .onErrorResumeNext(e -> {
                    initialized.set(false);
                    return Single.error(new SherlockException("Could not initialize locks table index", e));
                });
    }

    private Single<Connection> createConnectionWithRetry() {
        // Retrying connection because of a bug in connection pool
        // https://github.com/r2dbc/r2dbc-pool/issues/164
        // it seems that retrying connection once solves the issue
        return Single.<Connection>fromPublisher(connectionFactory.create())
                .onErrorResumeNext(e -> {
                    logger.debug("Could ne create connection. Retrying one more time", e);
                    return Single.fromPublisher(connectionFactory.create());
                })
                .doOnSuccess(c -> c.setAutoCommit(true));
    }

    private Single<Connection> commit(Connection connection) {
        return Flowable.fromPublisher(connection.commitTransaction())
                .firstElement()
                .map(__ -> connection)
                .toSingle(connection);
    }
}
