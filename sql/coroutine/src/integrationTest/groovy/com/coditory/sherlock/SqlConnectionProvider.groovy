package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import groovy.transform.CompileStatic
import io.r2dbc.spi.ConnectionFactory

import java.sql.Connection

@CompileStatic
interface SqlConnectionProvider {
    ConnectionFactory getConnectionFactory();

    Connection getBlockingConnection();
}

@CompileStatic
trait PostgresConnectionProvider implements SqlConnectionProvider, DatabaseManager {
    BindingMapper bindingMapper = BindingMapper.POSTGRES_MAPPER

    @Override
    ConnectionFactory getConnectionFactory() {
        return KtPostgresHolder.getConnectionFactory()
    }

    @Override
    Connection getBlockingConnection() {
        return KtPostgresHolder.getBlockingConnection()
    }

    @Override
    void stopDatabase() {
        KtPostgresHolder.stopDb()
    }

    @Override
    void startDatabase() {
        KtPostgresHolder.startDb()
    }
}

@CompileStatic
trait MySqlConnectionProvider implements SqlConnectionProvider, DatabaseManager {
    BindingMapper bindingMapper = BindingMapper.MYSQL_MAPPER

    @Override
    ConnectionFactory getConnectionFactory() {
        return KtMySqlHolder.getConnectionFactory()
    }

    @Override
    Connection getBlockingConnection() {
        return KtMySqlHolder.getBlockingConnection()
    }

    @Override
    void stopDatabase() {
        KtMySqlHolder.stopDb()
    }

    @Override
    void startDatabase() {
        KtMySqlHolder.startDb()
    }
}
