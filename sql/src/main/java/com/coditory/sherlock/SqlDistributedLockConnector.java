package com.coditory.sherlock;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Clock;
import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class SqlDistributedLockConnector implements DistributedLockConnector {
    private final ConnectionPool connectionPool;
    private final SqlTableInitializer sqlTableInitializer;
    private final SqlQueries sqlQueries;
    private final Clock clock;

    SqlDistributedLockConnector(
            ConnectionPool connectionPool, String tableName, Clock clock) {
        this.clock = expectNonNull(clock, "Expected non null clock");
        this.sqlQueries = new SqlQueries(tableName);
        this.connectionPool = connectionPool;
        this.sqlTableInitializer = new SqlTableInitializer(sqlQueries);
    }

    @Override
    public void initialize() {
        try (SqlConnection connection = connectionPool.getConnection()) {
            sqlTableInitializer.initialize(connection);
        } catch (Throwable e) {
            throw new SherlockException("Could not initialize SQL table", e);
        }
    }

    @Override
    public boolean acquire(LockRequest lockRequest) {
        Instant now = now();
        try (SqlConnection connection = connectionPool.getConnection()) {
            return updateReleasedLock(connection, lockRequest, now)
                    || insertLock(connection, lockRequest, now);
        } catch (Throwable e) {
            throw new SherlockException("Could not acquire lock: " + lockRequest, e);
        }
    }

    @Override
    public boolean acquireOrProlong(LockRequest lockRequest) {
        Instant now = now();
        try (SqlConnection connection = connectionPool.getConnection()) {
            return updateAcquiredOrReleasedLock(connection, lockRequest, now)
                    || insertLock(connection, lockRequest, now);
        } catch (Throwable e) {
            throw new SherlockException("Could not acquire or prolong lock: " + lockRequest, e);
        }
    }

    @Override
    public boolean forceAcquire(LockRequest lockRequest) {
        Instant now = now();
        try (SqlConnection connection = connectionPool.getConnection()) {
            return updateLockById(connection, lockRequest, now)
                    || insertLock(connection, lockRequest, now);
        } catch (Throwable e) {
            throw new SherlockException("Could not force acquire lock: " + lockRequest, e);
        }
    }

    private boolean updateReleasedLock(SqlConnection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.getLockId().getValue();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        try (PreparedStatement statement = getStatement(connection, sqlQueries.updateReleasedLock())) {
            statement.setString(1, lockRequest.getOwnerId().getValue());
            statement.setTimestamp(2, timestamp(now));
            setupOptionalTimestamp(statement, 3, expiresAt);
            statement.setString(4, lockId);
            statement.setTimestamp(5, timestamp(now));
            return statement.executeUpdate() > 0;
        }
    }

    private boolean updateAcquiredOrReleasedLock(SqlConnection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.getLockId().getValue();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        try (PreparedStatement statement = getStatement(connection, sqlQueries.updateAcquiredOrReleasedLock())) {
            statement.setString(1, lockRequest.getOwnerId().getValue());
            statement.setTimestamp(2, timestamp(now));
            setupOptionalTimestamp(statement, 3, expiresAt);
            statement.setString(4, lockId);
            statement.setString(5, lockRequest.getOwnerId().getValue());
            statement.setTimestamp(6, timestamp(now));
            return statement.executeUpdate() > 0;
        }
    }

    private boolean updateLockById(SqlConnection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.getLockId().getValue();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        try (PreparedStatement statement = getStatement(connection, sqlQueries.updateLockById())) {
            statement.setString(1, lockRequest.getOwnerId().getValue());
            statement.setTimestamp(2, timestamp(now));
            setupOptionalTimestamp(statement, 3, expiresAt);
            statement.setString(4, lockId);
            return statement.executeUpdate() > 0;
        }
    }

    private boolean insertLock(SqlConnection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.getLockId().getValue();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        try (PreparedStatement statement = getStatement(connection, sqlQueries.insertLock())) {
            statement.setString(1, lockId);
            statement.setString(2, lockRequest.getOwnerId().getValue());
            statement.setTimestamp(3, timestamp(now));
            setupOptionalTimestamp(statement, 4, expiresAt);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean release(LockId lockId, OwnerId ownerId) {
        try (
                SqlConnection connection = connectionPool.getConnection();
                PreparedStatement statement = getStatement(connection, sqlQueries.deleteAcquiredByIdAndOwnerId())
        ) {
            statement.setString(1, lockId.getValue());
            statement.setString(2, ownerId.getValue());
            statement.setTimestamp(3, timestamp(now()));
            return statement.executeUpdate() > 0;
        } catch (Throwable e) {
            throw new SherlockException("Could not release lock: " + lockId.getValue() + ", owner: " + ownerId, e);
        }
    }

    @Override
    public boolean forceRelease(LockId lockId) {
        try (
                SqlConnection connection = connectionPool.getConnection();
                PreparedStatement statement = getStatement(connection, sqlQueries.deleteAcquiredById())
        ) {
            statement.setString(1, lockId.getValue());
            statement.setTimestamp(2, timestamp(now()));
            return statement.executeUpdate() > 0;
        } catch (Throwable e) {
            throw new SherlockException("Could not force release lock: " + lockId.getValue(), e);
        }
    }

    @Override
    public boolean forceReleaseAll() {
        try (
                SqlConnection connection = connectionPool.getConnection();
                PreparedStatement statement = getStatement(connection, sqlQueries.deleteAll())
        ) {
            return statement.executeUpdate() > 0;
        } catch (Throwable e) {
            throw new IllegalStateException("Could not force release all locks", e);
        }
    }

    private Instant now() {
        return clock.instant();
    }

    private Instant expiresAt(Instant now, LockDuration duration) {
        if (duration == null || duration.getValue() == null) {
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

    private PreparedStatement getStatement(SqlConnection connection, String sql) {
        return sqlTableInitializer.getInitializedStatement(connection, sql);
    }
}
