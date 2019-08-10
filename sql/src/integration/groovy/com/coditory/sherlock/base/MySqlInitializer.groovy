package com.coditory.sherlock.base

import groovy.transform.CompileStatic
import org.testcontainers.containers.GenericContainer

import java.sql.Connection
import java.sql.DriverManager

@CompileStatic
class MySqlInitializer {
  static final Connection connection = startMySql()

  private static Connection startMySql() {
    GenericContainer mysql = new GenericContainer<>("mysql:8")
      .withExposedPorts(3306)
    mysql.setCommand("--default-authentication-plugin=mysql_native_password")
    mysql.addEnv("MYSQL_ROOT_PASSWORD", "mysql")
    mysql.addEnv("MYSQL_USER", "mysql")
    mysql.addEnv("MYSQL_PASSWORD", "mysql")
    mysql.addEnv("MYSQL_DATABASE", "mysql")
    mysql.start()
    Properties connectionProps = new Properties();
    connectionProps.put("user", "mysql");
    connectionProps.put("password", "mysql");
    return DriverManager.getConnection("jdbc:mysql://localhost:${mysql.firstMappedPort}/mysql", connectionProps)
  }
}
