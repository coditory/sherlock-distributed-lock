package com.coditory.sherlock;

import io.r2dbc.spi.ConnectionFactory;

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
    private BindingParameterMapper bindingParameterMapper;

    /**
     * @return new instance of the builder
     */
    public static ReactorSqlSherlockBuilder reactorSqlSherlock() {
        return new ReactorSqlSherlockBuilder();
    }

    /**
     * @param connectionFactory the connection factory to the database
     * @return sql sherlock with default configuration
     */
    public static ReactorSqlSherlockBuilder reactorSqlSherlock(ConnectionFactory connectionFactory) {
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
    public ReactorSqlSherlockBuilder withConnectionFactory(ConnectionFactory connectionFactory) {
        expectNonNull(connectionFactory, "Expected non null connectionFactory");
        this.connectionFactory = connectionFactory;
        return this;
    }

    /**
     * Parameterized statements are vendor specific.
     * That's why you must specify the binding notation with a bindingParameterMapper.
     *
     * @param bindingParameterMapper the connection pool to the database
     * @return the instance
     * @link https://r2dbc.io/spec/1.0.0.RELEASE/spec/html/#statements.parameterized
     */
    public ReactorSqlSherlockBuilder withBindingParameterMapper(BindingParameterMapper bindingParameterMapper) {
        expectNonNull(bindingParameterMapper, "Expected non null bindingParameterMapper");
        this.bindingParameterMapper = bindingParameterMapper;
        return this;
    }

    /**
     * @param tableName the name of the table that stores locks
     * @return the instance
     */
    public ReactorSqlSherlockBuilder withLocksTable(String tableName) {
        this.tableName = expectNonEmpty(tableName, "Expected non empty tableName");
        return this;
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    public ReactorSqlSherlockBuilder withClock(Clock clock) {
        this.clock = expectNonNull(clock, "Expected non null clock");
        return this;
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    public ReactorSherlock build() {
        expectNonNull(connectionFactory, "connectionFactory");
        expectNonNull(bindingParameterMapper, "bindingParameterMapper");
        BindingParameterMapper bindingMapper = PrecomputedBindingParameterMapper.from(bindingParameterMapper);
        ReactorSqlDistributedLockConnector connector = new ReactorSqlDistributedLockConnector(
                connectionFactory, tableName, clock, bindingMapper);
        return super.build(connector);
    }
}
