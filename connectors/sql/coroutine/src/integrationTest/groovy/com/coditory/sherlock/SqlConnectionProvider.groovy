package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.sql.BindingMapper
import com.coditory.sherlock.sql.MySqlHolder
import com.coditory.sherlock.sql.PostgresHolder
import groovy.transform.CompileStatic
import io.r2dbc.spi.ConnectionFactory

import javax.sql.DataSource
import java.sql.Connection

@CompileStatic
interface SqlConnectionProvider {
    ConnectionFactory getConnectionFactory();

    Connection getBlockingConnection();

    DataSource getDataSource();
}

@CompileStatic
trait PostgresConnectionProvider implements SqlConnectionProvider, DatabaseManager {
    BindingMapper bindingMapper = BindingMapper.POSTGRES_MAPPER

    @Override
    DataSource getDataSource() {
        return PostgresHolder.getDataSource()
    }

    @Override
    ConnectionFactory getConnectionFactory() {
        return PostgresConnectionPoolHolder.getConnectionFactory()
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
trait MySqlConnectionProvider implements SqlConnectionProvider, DatabaseManager {
    BindingMapper bindingMapper = BindingMapper.MYSQL_MAPPER

    @Override
    DataSource getDataSource() {
        return MySqlHolder.getDataSource()
    }

    @Override
    ConnectionFactory getConnectionFactory() {
        return MySqlConnectionPoolHolder.getConnectionFactory()
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
