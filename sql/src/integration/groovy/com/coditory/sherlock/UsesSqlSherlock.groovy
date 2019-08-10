package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.SqlInitializer.connection

trait UsesSqlSherlock implements DistributedLocksCreator {
  static final String locksTableName = "locks"

  @Override
  Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
    return SqlSherlock.builder()
      .withConnection(connection)
      .withLocksTable(locksTableName)
      .withOwnerId(instanceId)
      .withLockDuration(duration)
      .withClock(clock)
      .build()
  }
}
