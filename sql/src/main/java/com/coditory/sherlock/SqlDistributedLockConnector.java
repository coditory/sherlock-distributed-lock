package com.coditory.sherlock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Clock;
import java.time.Instant;

import static com.coditory.sherlock.util.Preconditions.expectNonNull;

class SqlDistributedLockConnector implements DistributedLockConnector {
  private final SqlTableInitializer sqlTableInitializer;
  private final SqlQueries sqlQueries;
  private final Clock clock;

  SqlDistributedLockConnector(
    Connection connection, String tableName, Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    this.sqlQueries = new SqlQueries(tableName);
    this.sqlTableInitializer = new SqlTableInitializer(sqlQueries, connection);
  }

  @Override
  public void initialize() {
    sqlTableInitializer.initialize();
  }

  @Override
  public boolean acquire(LockRequest lockRequest) {
    Instant now = now();
    return updateReleasedLock(lockRequest, now)
      || insertLock(lockRequest, now);
  }

  @Override
  public boolean acquireOrProlong(LockRequest lockRequest) {
    Instant now = now();
    return updateAcquiredOrReleasedLock(lockRequest, now)
      || insertLock(lockRequest, now);
  }

  @Override
  public boolean forceAcquire(LockRequest lockRequest) {
    Instant now = now();
    return updateLockById(lockRequest, now)
      || insertLock(lockRequest, now);
  }

  private boolean updateReleasedLock(LockRequest lockRequest, Instant now) {
    String lockId = lockRequest.getLockId().getValue();
    Instant expiresAt = expiresAt(now, lockRequest.getDuration());
    try (PreparedStatement statement = getStatement(sqlQueries.updateReleasedLock())) {
      statement.setString(1, lockRequest.getOwnerId().getValue());
      statement.setTimestamp(2, timestamp(now));
      setupOptionalTimestamp(statement, 3, expiresAt);
      statement.setString(4, lockId);
      statement.setTimestamp(5, timestamp(now));
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new IllegalStateException("SQL Error when updating a lock: " + lockId, e);
    }
  }

  private boolean updateAcquiredOrReleasedLock(LockRequest lockRequest, Instant now) {
    String lockId = lockRequest.getLockId().getValue();
    Instant expiresAt = expiresAt(now, lockRequest.getDuration());
    try (PreparedStatement statement = getStatement(sqlQueries.updateAcquiredOrReleasedLock())) {
      statement.setString(1, lockRequest.getOwnerId().getValue());
      statement.setTimestamp(2, timestamp(now));
      setupOptionalTimestamp(statement, 3, expiresAt);
      statement.setString(4, lockId);
      statement.setString(5, lockRequest.getOwnerId().getValue());
      statement.setTimestamp(6, timestamp(now));
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new IllegalStateException("SQL Error when updating a lock: " + lockId, e);
    }
  }

  private boolean updateLockById(LockRequest lockRequest, Instant now) {
    String lockId = lockRequest.getLockId().getValue();
    Instant expiresAt = expiresAt(now, lockRequest.getDuration());
    try (PreparedStatement statement = getStatement(sqlQueries.updateLockById())) {
      statement.setString(1, lockRequest.getOwnerId().getValue());
      statement.setTimestamp(2, timestamp(now));
      setupOptionalTimestamp(statement, 3, expiresAt);
      statement.setString(4, lockId);
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new IllegalStateException("SQL Error when updating a lock: " + lockId, e);
    }
  }

  private boolean insertLock(LockRequest lockRequest, Instant now) {
    String lockId = lockRequest.getLockId().getValue();
    Instant expiresAt = expiresAt(now, lockRequest.getDuration());
    try (PreparedStatement statement = getStatement(sqlQueries.insertLock())) {
      statement.setString(1, lockId);
      statement.setString(2, lockRequest.getOwnerId().getValue());
      statement.setTimestamp(3, timestamp(now));
      setupOptionalTimestamp(statement, 4, expiresAt);
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      if (!e.getMessage().contains("duplicate")) {
        throw new IllegalStateException("SQL Error when inserting a lock: " + lockId, e);
      }
      return false;
    }
  }

  @Override
  public boolean release(LockId lockId, OwnerId ownerId) {
    try (PreparedStatement statement = getStatement(sqlQueries.deleteAcquiredByIdAndOwnerId())) {
      statement.setString(1, lockId.getValue());
      statement.setString(2, ownerId.getValue());
      statement.setTimestamp(3, timestamp(now()));
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new IllegalStateException("Could not delete lock: " + lockId.getValue(), e);
    }
  }

  @Override
  public boolean forceRelease(LockId lockId) {
    try (PreparedStatement statement = getStatement(sqlQueries.deleteAcquiredById())) {
      statement.setString(1, lockId.getValue());
      statement.setTimestamp(2, timestamp(now()));
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new IllegalStateException("Could not delete lock: " + lockId.getValue(), e);
    }
  }

  @Override
  public boolean forceReleaseAll() {
    try (PreparedStatement statement = getStatement(sqlQueries.deleteAll())) {
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new IllegalStateException("Could not delete all locks", e);
    }
  }

  private Instant now() {
    return clock.instant();
  }

  private Instant expiresAt(Instant now, LockDuration duration) {
    if (duration == null) {
      return null;
    }
    return now.plus(duration.getValue());
  }

  private void setupOptionalTimestamp(PreparedStatement statement, int index, Instant instant)
    throws SQLException {
    if (instant != null) {
      statement.setTimestamp(index, timestamp(instant));
    } else {
      statement.setNull(index, Types.TIMESTAMP);
    }
  }

  private Timestamp timestamp(Instant instant) {
    return new Timestamp(instant.toEpochMilli());
  }

  private PreparedStatement getStatement(String sql) {
    return sqlTableInitializer.getInitializedStatement(sql);
  }
}
