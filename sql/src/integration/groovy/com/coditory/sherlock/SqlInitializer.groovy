package com.coditory.sherlock


import org.testcontainers.containers.GenericContainer

import java.sql.Connection
import java.sql.DriverManager

class SqlInitializer {
  static final Connection connection = startPostgres()
  static final String locksDBName = "postgres"

  private static Connection startPostgres() {
    // using an older version to preserve backward compatibility
    GenericContainer postgres = new GenericContainer<>("postgres:10")
      .withExposedPorts(5432)
    postgres.addEnv("POSTGRES_PASSWORD", "postgres")
    postgres.addEnv("POSTGRES_USER", "postgres")
    postgres.addEnv("POSTGRES_DB", locksDBName)
    postgres.start()
    Properties connectionProps = new Properties();
    connectionProps.put("user", "postgres");
    connectionProps.put("password", "postgres");
    return DriverManager.getConnection("jdbc:postgresql://localhost:${postgres.firstMappedPort}/", connectionProps)
  }
}
