package com.coditory.sherlock.base

import groovy.transform.CompileStatic
import org.testcontainers.containers.MySQLContainer

import java.sql.Connection
import java.sql.DriverManager

@CompileStatic
class MySqlHolder {
    private static Connection connection

    synchronized static Connection getConnection() {
        if (connection == null) {
            connection = startDb()
        }
        return connection
    }

    private static Connection startDb() {
        MySQLContainer db = new MySQLContainer("mysql:8")
        db.start()
        Properties properties = new Properties()
        properties.put("user", db.getUsername())
        properties.put("password", db.getPassword())
        return DriverManager.getConnection(db.getJdbcUrl(), properties)
    }
}
