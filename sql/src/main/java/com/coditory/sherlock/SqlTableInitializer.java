package com.coditory.sherlock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

class SqlTableInitializer {
  private final Connection connection;
  private final SqlQueries sqlQueries;
  private final AtomicBoolean initialized = new AtomicBoolean(false);

  SqlTableInitializer(SqlQueries sqlQueries, Connection connection) {
    this.connection = connection;
    this.sqlQueries = sqlQueries;
  }

  PreparedStatement getInitializedStatement(String sql) {
    if (initialized.compareAndSet(false, true)) {
      initialize();
    }
    try {
      return connection.prepareStatement(sql);
    } catch (SQLException e) {
      throw new IllegalStateException("Could not create SQL statement", e);
    }
  }

  void initialize() {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(sqlQueries.createLocksTable());
      // TODO: Setup indexes
      initialized.set(true);
    } catch (SQLException e) {
      try (PreparedStatement statement = connection
        .prepareStatement(sqlQueries.checkTableExits())) {
        statement.executeQuery();
        // if no error then table exists
        initialized.set(true);
      } catch (SQLException checkException) {
        throw new IllegalStateException("Could not initialize locks table", e);
      }
    }
  }
}
