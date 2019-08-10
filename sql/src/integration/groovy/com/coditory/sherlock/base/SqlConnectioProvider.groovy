package com.coditory.sherlock.base

import groovy.transform.CompileStatic

import java.sql.Connection

@CompileStatic
interface SqlConnectionProvider {
  Connection getConnection();
}

@CompileStatic
trait PostgresConnectionProvider implements SqlConnectionProvider {
  Connection getConnection() {
    return PostgresInitializer.connection;
  }
}

@CompileStatic
trait MySqlConnectionProvider implements SqlConnectionProvider {
  Connection getConnection() {
    return MySqlInitializer.connection;
  }
}
