package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator

import java.sql.Connection
import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.SqlSherlockBuilder.sqlSherlock

trait UsesSqlSherlock implements DistributedLocksCreator {
  static final String locksTableName = "locks"

  abstract Connection getConnection()

  @Override
  Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
    return sqlSherlock()
      .withConnection(connection)
      .withLocksTable(locksTableName)
      .withOwnerId(instanceId)
      .withLockDuration(duration)
      .withClock(clock)
      .build()
  }
}
