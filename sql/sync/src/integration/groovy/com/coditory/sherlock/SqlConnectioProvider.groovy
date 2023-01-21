package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import groovy.transform.CompileStatic

import javax.sql.DataSource

@CompileStatic
interface SqlConnectionProvider {
    DataSource getDataSource();
}

@CompileStatic
trait PostgresConnectionProvider implements SqlConnectionProvider, DatabaseManager {
    @Override
    DataSource getDataSource() {
        return PostgresHolder.getDataSource()
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
    DataSource getDataSource() {
        return MySqlHolder.getDataSource()
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
