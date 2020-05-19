package com.coditory.sherlock.base

import groovy.transform.CompileStatic
import org.testcontainers.containers.PostgreSQLContainer

import java.sql.Connection
import java.sql.DriverManager

@CompileStatic
class PostgresHolder {
    private static Connection connection = startDb()

    synchronized static Connection getConnection() {
        if (connection == null) {
            connection = startDb()
        }
        return connection
    }

    private static Connection startDb() {
        PostgreSQLContainer db = new PostgreSQLContainer("postgres:11")
        db.start()
        Properties properties = new Properties()
        properties.put("user", db.getUsername())
        properties.put("password", db.getPassword())
        return DriverManager.getConnection(db.getJdbcUrl(), properties)
    }
}
