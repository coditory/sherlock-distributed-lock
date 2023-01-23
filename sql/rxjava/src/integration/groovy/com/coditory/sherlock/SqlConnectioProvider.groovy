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
        return RxPostgresHolder.getConnectionFactory()
    }

    @Override
    Connection getBlockingConnection() {
        return RxPostgresHolder.getBlockingConnection()
    }

    @Override
    void stopDatabase() {
        RxPostgresHolder.stopDb()
    }

    @Override
    void startDatabase() {
        RxPostgresHolder.startDb()
    }
}

@CompileStatic
trait MySqlConnectionProvider implements SqlConnectionFactoryProvider, DatabaseManager {
    BindingMapper bindingMapper = BindingMapper.MYSQL_MAPPER

    @Override
    ConnectionFactory getConnectionFactory() {
        return RxMySqlHolder.getConnectionFactory()
    }

    @Override
    Connection getBlockingConnection() {
        return RxMySqlHolder.getBlockingConnection()
    }

    @Override
    void stopDatabase() {
        RxMySqlHolder.stopDb()
    }

    @Override
    void startDatabase() {
        RxMySqlHolder.startDb()
    }
}
