package com.coditory.sherlock;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Clock;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;

/**
 * Builds {@link Sherlock} that uses SQL database for locking mechanism.
 */
public final class SqlSherlockBuilder extends SherlockWithConnectorBuilder<SqlSherlockBuilder> {
    public static final String DEFAULT_LOCKS_TABLE_NAME = "locks";
    private String tableName = DEFAULT_LOCKS_TABLE_NAME;
    private Clock clock = DEFAULT_CLOCK;
    private ConnectionPool connectionPool;

    /**
     * @return new instance of the builder
     */
    public static SqlSherlockBuilder sqlSherlock() {
        return new SqlSherlockBuilder();
    }

    /**
     * @param connection the connection to the database
     * @return sql sherlock with default configuration
     * @see <a href="https://github.com/coditory/sherlock-distributed-lock/issues/79">Recovery after DB outage is failing</a>
     * @deprecated Use {@link #withConnectionPool(DataSource)} instead.
     */
    @Deprecated(since = "0.4.17")
    public static Sherlock sqlSherlock(Connection connection) {
        return sqlSherlock()
            .withConnection(connection)
            .build();
    }

    /**
     * @param connectionPool the connection to the database
     * @return sql sherlock with default configuration
     */
    public static Sherlock sqlSherlock(DataSource connectionPool) {
        return sqlSherlock()
            .withConnectionPool(connectionPool)
            .build();
    }

    private SqlSherlockBuilder() {
        // deliberately empty
    }

    /**
     * @param connection the connection to the database
     * @return the instance
     * @see <a href="https://github.com/coditory/sherlock-distributed-lock/issues/79">Recovery after DB outage is failing</a>
     * @deprecated Use {@link #withConnectionPool(DataSource)} instead.
     */
    @Deprecated(since = "0.4.17")
    public SqlSherlockBuilder withConnection(Connection connection) {
        expectNonNull(connection, "Expected non null connection");
        this.connectionPool = ConnectionPool.of(connection);
        return this;
    }

    /**
     * @param connectionPool the connection pool to the database
     * @return the instance
     */
    public SqlSherlockBuilder withConnectionPool(DataSource connectionPool) {
        expectNonNull(connectionPool, "Expected non null connectionPool");
        this.connectionPool = ConnectionPool.of(connectionPool);
        return this;
    }

    /**
     * @param tableName the name of the table that stores locks
     * @return the instance
     */
    public SqlSherlockBuilder withLocksTable(String tableName) {
        this.tableName = expectNonEmpty(tableName, "Expected non empty tableName");
        return this;
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    public SqlSherlockBuilder withClock(Clock clock) {
        this.clock = expectNonNull(clock, "Expected non null clock");
        return this;
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    public Sherlock build() {
        expectNonNull(connectionPool, "connectionPool");
        SqlDistributedLockConnector connector = new SqlDistributedLockConnector(
            connectionPool, tableName, clock);
        return super.build(connector);
    }
}
