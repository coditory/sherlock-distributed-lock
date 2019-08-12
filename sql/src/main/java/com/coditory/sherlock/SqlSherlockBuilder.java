package com.coditory.sherlock;

import java.sql.Connection;
import java.time.Clock;

import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * Builds {@link Sherlock} that uses SQL database for locking mechanism.
 */
public final class SqlSherlockBuilder extends SherlockWithConnectorBuilder<SqlSherlockBuilder> {
  public static final String DEFAULT_LOCKS_TABLE_NAME = "locks";
  private String tableName = DEFAULT_LOCKS_TABLE_NAME;
  private Clock clock = DEFAULT_CLOCK;
  private Connection connection;

  /**
   * @return new instance of the builder
   */
  public static SqlSherlockBuilder sqlSherlock() {
    return new SqlSherlockBuilder();
  }

  /**
   * @param connection the connection to the database
   * @return sql sherlock with default configuration
   */
  public static Sherlock sqlSherlock(Connection connection) {
    return sqlSherlock()
      .withConnection(connection)
      .build();
  }

  private SqlSherlockBuilder() {
    // deliberately empty
  }

  /**
   * @param connection the connection to the database
   * @return the instance
   */
  public SqlSherlockBuilder withConnection(Connection connection) {
    this.connection = expectNonNull(connection, "Expected non null connection");
    return this;
  }

  /**
   * @param tableName the name of the table that stores locks
   * @return the instance
   */
  public SqlSherlockBuilder withLocksTable(String tableName) {
    this.tableName = expectNonEmpty(tableName, "Expected non empty tableName");
    return this;
  }

  /**
   * @param clock time provider used in locking mechanism. Default: {@link
   *   SherlockDefaults#DEFAULT_CLOCK}
   * @return the instance
   */
  public SqlSherlockBuilder withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  /**
   * @return sherlock instance
   * @throws IllegalArgumentException when some required values are missing
   */
  public Sherlock build() {
    expectNonNull(connection, "Expected non null connection");
    SqlDistributedLockConnector connector = new SqlDistributedLockConnector(
      connection, tableName, clock);
    return super.build(connector);
  }
}
