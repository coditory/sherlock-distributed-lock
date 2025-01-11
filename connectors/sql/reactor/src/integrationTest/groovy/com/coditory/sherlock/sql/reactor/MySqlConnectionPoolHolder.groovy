package com.coditory.sherlock.sql.reactor

import com.coditory.sherlock.sql.MySqlHolder
import groovy.transform.CompileStatic
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.Option

@CompileStatic
class MySqlConnectionPoolHolder {
    private static ConnectionFactory connectionFactory = null

    synchronized static ConnectionFactory getConnectionFactory() {
        MySqlHolder.startDb()
        if (connectionFactory != null) {
            return connectionFactory
        }
        ConnectionFactoryOptions options = ConnectionFactoryOptions
            .parse(MySqlHolder.getJdbcUrl().replace("jdbc:", "r2dbc:"))
            .mutate()
            .option(Option.valueOf("connectionTimeZone"), "UTC")
            .option(ConnectionFactoryOptions.USER, MySqlHolder.getUsername())
            .option(ConnectionFactoryOptions.PASSWORD, MySqlHolder.getPassword())
            .option(ConnectionFactoryOptions.DATABASE, MySqlHolder.getDatabaseName())
            .build()
        connectionFactory = ConnectionFactories.get(options)
        return connectionFactory
    }
}
