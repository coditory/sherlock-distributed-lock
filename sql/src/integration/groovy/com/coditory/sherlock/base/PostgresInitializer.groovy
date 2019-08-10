package com.coditory.sherlock.base

import groovy.transform.CompileStatic
import org.testcontainers.containers.GenericContainer

import java.sql.Connection
import java.sql.DriverManager

@CompileStatic
class PostgresInitializer {
  static final Connection connection = startPostgres()

  private static Connection startPostgres() {
    GenericContainer postgres = new GenericContainer<>("postgres:11")
      .withExposedPorts(5432)
    postgres.addEnv("POSTGRES_PASSWORD", "postgres")
    postgres.addEnv("POSTGRES_USER", "postgres")
    postgres.addEnv("POSTGRES_DB", "postgres")
    postgres.start()
    Properties connectionProps = new Properties();
    connectionProps.put("user", "postgres");
    connectionProps.put("password", "postgres");
    return DriverManager.getConnection("jdbc:postgresql://localhost:${postgres.firstMappedPort}/postgres", connectionProps)
  }
}
