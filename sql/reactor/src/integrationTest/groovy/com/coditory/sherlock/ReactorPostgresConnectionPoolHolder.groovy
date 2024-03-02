package com.coditory.sherlock

import groovy.transform.CompileStatic
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ValidationDepth

import java.time.Duration

@CompileStatic
class ReactorPostgresConnectionPoolHolder {
    private static ConnectionFactory connectionFactory = null

    synchronized static ConnectionFactory getConnectionFactory() {
        PostgresHolder.startDb()
        if (connectionFactory == null) {
            connectionFactory = pooledConnectionFactory()
        }
        return connectionFactory
    }

    private static ConnectionFactory pooledConnectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions
                .parse(PostgresHolder.getJdbcUrl().replace("jdbc:", "r2dbc:"))
                .mutate()
                .option(ConnectionFactoryOptions.USER, PostgresHolder.getUsername())
                .option(ConnectionFactoryOptions.PASSWORD, PostgresHolder.getPassword())
                .option(ConnectionFactoryOptions.DATABASE, PostgresHolder.getDatabaseName())
                .build()
        ConnectionFactory connectionFactory = ConnectionFactories.get(options)
        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
                .maxIdleTime(Duration.ofSeconds(1))
                .maxAcquireTime(Duration.ofSeconds(10))
                .initialSize(10)
                .minIdle(3)
                .maxSize(10)
                .acquireRetry(5)
                .maxValidationTime(Duration.ofSeconds(1))
                .validationQuery("SELECT 1")
                .validationDepth(ValidationDepth.REMOTE)
                .build()
        return new ConnectionPool(configuration)
    }
}
