package com.coditory.sherlock.sql;

import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.SherlockDefaults;
import com.coditory.sherlock.SherlockWithConnectorBuilder;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
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
    private DataSource dataSource;

    /**
     * @return new instance of the builder
     */
    @NotNull
    public static SqlSherlockBuilder sqlSherlock() {
        return new SqlSherlockBuilder();
    }

    /**
     * @param dataSource the connection pool to the database
     * @return sql sherlock with default configuration
     */
    @NotNull
    public static Sherlock sqlSherlock(@NotNull DataSource dataSource) {
        expectNonNull(dataSource, "dataSource");
        return sqlSherlock()
                .withDataSource(dataSource)
                .build();
    }

    private SqlSherlockBuilder() {
        // deliberately empty
    }

    /**
     * @param dataSource the connection pool to the database
     * @return the instance
     */
    @NotNull
    public SqlSherlockBuilder withDataSource(@NotNull DataSource dataSource) {
        expectNonNull(dataSource, "dataSource");
        this.dataSource = dataSource;
        return this;
    }

    /**
     * @param tableName the name of the table that stores locks
     * @return the instance
     */
    @NotNull
    public SqlSherlockBuilder withLocksTable(@NotNull String tableName) {
        this.tableName = expectNonEmpty(tableName, "tableName");
        return this;
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    @NotNull
    public SqlSherlockBuilder withClock(@NotNull Clock clock) {
        this.clock = expectNonNull(clock, "clock");
        return this;
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    @NotNull
    public Sherlock build() {
        expectNonNull(dataSource, "dataSource");
        SqlDistributedLockConnector connector = new SqlDistributedLockConnector(dataSource, tableName, clock);
        return super.build(connector);
    }
}
