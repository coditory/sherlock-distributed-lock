package com.coditory.sherlock.sample;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.util.Properties;

import static com.coditory.sherlock.SqlSherlockBuilder.sqlSherlock;

public class SqlSyncSample {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private Connection dbConnection() {
    Properties connectionProps = new Properties();
    connectionProps.put("user", "mysql");
    connectionProps.put("password", "mysql");
    try {
      return DriverManager
        .getConnection("jdbc:mysql://localhost:${mysql.firstMappedPort}/mysql", connectionProps);
    } catch (SQLException e) {
      throw new RuntimeException("Could not create MySQL connection", e);
    }
  }

  void sampleSqlSherlock() {
    Sherlock sherlock = sqlSherlock()
      .withClock(Clock.systemDefaultZone())
      .withLockDuration(Duration.ofMinutes(5))
      .withUniqueOwnerId()
      .withConnection(dbConnection())
      .withLocksTable("LOCKS")
      .build();
    // ...or simply
    // Sherlock sherlockWithDefaults = sqlSherlock(dbConnection());
    DistributedLock lock = sherlock.createLock("sample-lock");
    lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
  }

  public static void main(String[] args) {
    new SqlSyncSample().sampleSqlSherlock();
  }
}
