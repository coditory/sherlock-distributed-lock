package com.coditory.sherlock.base

import groovy.transform.CompileStatic

import java.sql.Connection

@CompileStatic
interface SqlConnectionProvider {
    Connection getConnection();
}

@CompileStatic
trait PostgresConnectionProvider implements SqlConnectionProvider {
    @Override
    Connection getConnection() {
        return PostgresHolder.getConnection()
    }
}

@CompileStatic
trait MySqlConnectionProvider implements SqlConnectionProvider {
    @Override
    Connection getConnection() {
        return MySqlHolder.getConnection()
    }
}
