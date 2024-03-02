package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import groovy.transform.CompileStatic
import io.r2dbc.spi.ConnectionFactory

import javax.sql.DataSource
import java.sql.Connection

@CompileStatic
interface SqlConnectionFactoryProvider {
    ConnectionFactory getConnectionFactory();

    Connection getBlockingConnection();

    DataSource getDataSource();
}

@CompileStatic
trait PostgresConnectionProvider implements SqlConnectionFactoryProvider, DatabaseManager {
    BindingMapper bindingMapper = BindingMapper.POSTGRES_MAPPER

    @Override
    DataSource getDataSource() {
        return PostgresHolder.getDataSource()
    }

    @Override
    ConnectionFactory getConnectionFactory() {
        return RxPostgresConnectionPoolHolder.getConnectionFactory()
    }

    @Override
    Connection getBlockingConnection() {
        return PostgresHolder.getConnection()
    }

    @Override
    void stopDatabase() {
        PostgresHolder.stopDb()
    }

    @Override
    void startDatabase() {
        PostgresHolder.startDb()
    }
}

@CompileStatic
trait MySqlConnectionProvider implements SqlConnectionFactoryProvider, DatabaseManager {
    BindingMapper bindingMapper = BindingMapper.MYSQL_MAPPER

    @Override
    DataSource getDataSource() {
        return MySqlHolder.getDataSource()
    }

    @Override
    ConnectionFactory getConnectionFactory() {
        return RxMySqlConnectionPoolHolder.getConnectionFactory()
    }

    @Override
    Connection getBlockingConnection() {
        return MySqlHolder.getConnection()
    }

    @Override
    void stopDatabase() {
        MySqlHolder.stopDb()
    }

    @Override
    void startDatabase() {
        MySqlHolder.startDb()
    }
}
