package com.coditory.sherlock.sql.coroutines;

import com.coditory.sherlock.SherlockDefaults;
import com.coditory.sherlock.reactor.ReactorSherlock;
import com.coditory.sherlock.reactor.ReactorSherlockWithConnectorBuilder;
import com.coditory.sherlock.sql.BindingMapper;
import com.coditory.sherlock.sql.PrecomputedBindingParameterMapper;
import io.r2dbc.spi.ConnectionFactory;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;

/**
 * Builds {@link ReactorSherlock} that uses SQL database for locking mechanism.
 */
public final class ReactorSqlSherlockBuilder extends ReactorSherlockWithConnectorBuilder<ReactorSqlSherlockBuilder> {
    public static final String DEFAULT_LOCKS_TABLE_NAME = "locks";
    private String tableName = DEFAULT_LOCKS_TABLE_NAME;
    private Clock clock = DEFAULT_CLOCK;
    private ConnectionFactory connectionFactory;
    private BindingMapper bindingMapper;

    /**
     * @return new instance of the builder
     */
    @NotNull
    public static ReactorSqlSherlockBuilder reactorSqlSherlock() {
        return new ReactorSqlSherlockBuilder();
    }

    /**
     * @param connectionFactory the connection factory to the database
     * @return sql sherlock with default configuration
     */
    @NotNull
    public static ReactorSqlSherlockBuilder reactorSqlSherlock(@NotNull ConnectionFactory connectionFactory) {
        expectNonNull(connectionFactory, "connectionFactory");
        return reactorSqlSherlock()
                .withConnectionFactory(connectionFactory);
    }

    private ReactorSqlSherlockBuilder() {
        // deliberately empty
    }

    /**
     * @param connectionFactory the connection pool to the database
     * @return the instance
     */
    @NotNull
    public ReactorSqlSherlockBuilder withConnectionFactory(@NotNull ConnectionFactory connectionFactory) {
        expectNonNull(connectionFactory, "connectionFactory");
        this.connectionFactory = connectionFactory;
        return this;
    }

    /**
     * Parameterized statements are vendor specific.
     * That's why you must specify the binding notation with a bindingParameterMapper.
     *
     * @param bindingMapper the connection pool to the database
     * @return the instance
     * @link https://r2dbc.io/spec/1.0.0.RELEASE/spec/html/#statements.parameterized
     */
    @NotNull
    public ReactorSqlSherlockBuilder withBindingMapper(@NotNull BindingMapper bindingMapper) {
        expectNonNull(bindingMapper, "bindingMapper");
        this.bindingMapper = bindingMapper;
        return this;
    }

    /**
     * @param tableName the name of the table that stores locks
     * @return the instance
     */
    @NotNull
    public ReactorSqlSherlockBuilder withLocksTable(@NotNull String tableName) {
        expectNonEmpty(tableName, "tableName");
        this.tableName = tableName;
        return this;
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    @NotNull
    public ReactorSqlSherlockBuilder withClock(@NotNull Clock clock) {
        this.clock = expectNonNull(clock, "clock");
        return this;
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    @NotNull
    public ReactorSherlock build() {
        expectNonNull(connectionFactory, "connectionFactory");
        expectNonNull(bindingMapper, "bindingParameterMapper");
        BindingMapper bindingMapper = PrecomputedBindingParameterMapper.from(this.bindingMapper);
        ReactorSqlDistributedLockConnector connector = new ReactorSqlDistributedLockConnector(
                connectionFactory, tableName, clock, bindingMapper);
        return super.build(connector);
    }
}
