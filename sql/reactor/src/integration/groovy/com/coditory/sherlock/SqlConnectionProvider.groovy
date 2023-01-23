package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import groovy.transform.CompileStatic
import io.r2dbc.spi.ConnectionFactory

import java.sql.Connection

@CompileStatic
interface SqlConnectionFactoryProvider {
    ConnectionFactory getConnectionFactory();

    Connection getBlockingConnection();
}

@CompileStatic
trait PostgresConnectionProvider implements SqlConnectionFactoryProvider, DatabaseManager {
    BindingMapper bindingMapper = BindingMapper.POSTGRES_MAPPER

    @Override
    ConnectionFactory getConnectionFactory() {
        return ReactorPostgresHolder.getConnectionFactory()
    }

    @Override
    Connection getBlockingConnection() {
        return ReactorPostgresHolder.getBlockingConnection()
    }

    @Override
    void stopDatabase() {
        ReactorPostgresHolder.stopDb()
    }

    @Override
    void startDatabase() {
        ReactorPostgresHolder.startDb()
    }
}

@CompileStatic
trait MySqlConnectionProvider implements SqlConnectionFactoryProvider, DatabaseManager {
    BindingMapper bindingMapper = BindingMapper.MYSQL_MAPPER

    @Override
    ConnectionFactory getConnectionFactory() {
        return ReactorMySqlHolder.getConnectionFactory()
    }

    @Override
    Connection getBlockingConnection() {
        return ReactorMySqlHolder.getBlockingConnection()
    }

    @Override
    void stopDatabase() {
        ReactorMySqlHolder.stopDb()
    }

    @Override
    void startDatabase() {
        ReactorMySqlHolder.startDb()
    }
}
