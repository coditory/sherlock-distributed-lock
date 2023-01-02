package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import groovy.transform.CompileStatic

import javax.sql.DataSource

@CompileStatic
interface SqlConnectionProvider {
    DataSource getConnectionPool();
}

@CompileStatic
trait PostgresConnectionProvider implements SqlConnectionProvider, DatabaseManager {
    @Override
    DataSource getConnectionPool() {
        return PostgresHolder.getConnectionPool()
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
    @Override
    DataSource getConnectionPool() {
        return MySqlHolder.getConnectionPool()
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
