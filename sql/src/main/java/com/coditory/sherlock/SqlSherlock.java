package com.coditory.sherlock;

import java.sql.Connection;
import java.time.Clock;

import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.util.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.util.Preconditions.expectNonNull;

/**
 * Builds {@link Sherlock} that uses SQL database for locking mechanism.
 */
public final class SqlSherlock extends SherlockWithConnectorBuilder<SqlSherlock> {
  private String tableName = "locks";
  private Clock clock = DEFAULT_CLOCK;
  private Connection connection;

  /**
   * @return new instance of the builder
   */
  public static SqlSherlock builder() {
    return new SqlSherlock();
  }

  private SqlSherlock() {
    // deliberately empty
  }

  /**
   * @param connection the connection to the database
   * @return the instance
   */
  public SqlSherlock withConnection(Connection connection) {
    this.connection = expectNonNull(connection, "Expected non null connection");
    return this;
  }

  /**
   * @param tableName the name of the table that stores locks
   * @return the instance
   */
  public SqlSherlock withLocksTable(String tableName) {
    this.tableName = expectNonEmpty(tableName, "Expected non empty tableName");
    return this;
  }

  /**
   * @param clock time provider used in locking mechanism. Default: {@link
   *   SherlockDefaults#DEFAULT_CLOCK}
   * @return the instance
   */
  public SqlSherlock withClock(Clock clock) {
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
