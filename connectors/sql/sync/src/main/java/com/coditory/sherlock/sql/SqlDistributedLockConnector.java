package com.coditory.sherlock.sql;

import com.coditory.sherlock.DistributedLockConnector;
import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.SherlockException;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

class SqlDistributedLockConnector implements DistributedLockConnector {
    private final SqlTableInitializer sqlTableInitializer;
    private final SqlLockQueries sqlQueries;
    private final Clock clock;

    SqlDistributedLockConnector(
        DataSource dataSource,
        String tableName,
        Clock clock
    ) {
        expectNonNull(dataSource, "dataSource");
        expectNonEmpty(tableName, "tableName");
        expectNonNull(clock, "clock");
        this.clock = clock;
        this.sqlQueries = new SqlLockQueries(tableName);
        this.sqlTableInitializer = new SqlTableInitializer(sqlQueries, dataSource);
    }

    @Override
    public void initialize() {
        try {
            Connection connection = getInitializedConnection();
            connection.close();
        } catch (Throwable e) {
            throw new SherlockException("Could not initialize SQL table", e);
        }
    }

    @Override
    public boolean acquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        Instant now = now();
        try (Connection connection = getInitializedConnection()) {
            return updateReleasedLock(connection, lockRequest, now)
                || insertLock(connection, lockRequest, now);
        } catch (Throwable e) {
            throw new SherlockException("Could not acquire lock: " + lockRequest, e);
        }
    }

    @Override
    public boolean acquireOrProlong(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        Instant now = now();
        try (Connection connection = getInitializedConnection()) {
            return updateAcquiredOrReleasedLock(connection, lockRequest, now)
                || insertLock(connection, lockRequest, now);
        } catch (Throwable e) {
            throw new SherlockException("Could not acquire or prolong lock: " + lockRequest, e);
        }
    }

    @Override
    public boolean forceAcquire(@NotNull LockRequest lockRequest) {
        expectNonNull(lockRequest, "lockRequest");
        Instant now = now();
        try (Connection connection = getInitializedConnection()) {
            return updateLockById(connection, lockRequest, now)
                || insertLock(connection, lockRequest, now);
        } catch (Throwable e) {
            throw new SherlockException("Could not force acquire lock: " + lockRequest, e);
        }
    }

    private boolean updateReleasedLock(Connection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.lockId();
        Instant expiresAt = expiresAt(now, lockRequest.duration());
        try (PreparedStatement statement = connection.prepareStatement(sqlQueries.updateReleasedLock())) {
            statement.setString(1, lockRequest.ownerId());
            statement.setTimestamp(2, timestamp(now));
            setupOptionalTimestamp(statement, 3, expiresAt);
            statement.setString(4, lockId);
            statement.setTimestamp(5, timestamp(now));
            return statement.executeUpdate() > 0;
        }
    }

    private boolean updateAcquiredOrReleasedLock(Connection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.lockId();
        Instant expiresAt = expiresAt(now, lockRequest.duration());
        try (PreparedStatement statement = connection.prepareStatement(sqlQueries.updateAcquiredOrReleasedLock())) {
            statement.setString(1, lockRequest.ownerId());
            statement.setTimestamp(2, timestamp(now));
            setupOptionalTimestamp(statement, 3, expiresAt);
            statement.setString(4, lockId);
            statement.setString(5, lockRequest.ownerId());
            statement.setTimestamp(6, timestamp(now));
            return statement.executeUpdate() > 0;
        }
    }

    private boolean updateLockById(Connection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.lockId();
        Instant expiresAt = expiresAt(now, lockRequest.duration());
        try (PreparedStatement statement = connection.prepareStatement(sqlQueries.updateLockById())) {
            statement.setString(1, lockRequest.ownerId());
            statement.setTimestamp(2, timestamp(now));
            setupOptionalTimestamp(statement, 3, expiresAt);
            statement.setString(4, lockId);
            return statement.executeUpdate() > 0;
        }
    }

    private boolean insertLock(Connection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.lockId();
        Instant expiresAt = expiresAt(now, lockRequest.duration());
        try (PreparedStatement statement = connection.prepareStatement(sqlQueries.insertLock())) {
            statement.setString(1, lockId);
            statement.setString(2, lockRequest.ownerId());
            statement.setTimestamp(3, timestamp(now));
            setupOptionalTimestamp(statement, 4, expiresAt);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean release(@NotNull String lockId, @NotNull String ownerId) {
        expectNonNull(lockId, "lockId");
        expectNonNull(ownerId, "ownerId");
        try (
            Connection connection = getInitializedConnection();
            PreparedStatement statement = connection.prepareStatement(sqlQueries.deleteAcquiredByIdAndOwnerId())
        ) {
            statement.setString(1, lockId);
            statement.setString(2, ownerId);
            statement.setTimestamp(3, timestamp(now()));
            return statement.executeUpdate() > 0;
        } catch (Throwable e) {
            throw new SherlockException("Could not release lock: " + lockId + ", owner: " + ownerId, e);
        }
    }

    @Override
    public boolean forceRelease(@NotNull String lockId) {
        expectNonNull(lockId, "lockId");
        try (
            Connection connection = getInitializedConnection();
            PreparedStatement statement = connection.prepareStatement(sqlQueries.deleteAcquiredById())
        ) {
            statement.setString(1, lockId);
            statement.setTimestamp(2, timestamp(now()));
            return statement.executeUpdate() > 0;
        } catch (Throwable e) {
            throw new SherlockException("Could not force release lock: " + lockId, e);
        }
    }

    @Override
    public boolean forceReleaseAll() {
        try (
            Connection connection = getInitializedConnection();
            PreparedStatement statement = connection.prepareStatement(sqlQueries.deleteAll())
        ) {
            return statement.executeUpdate() > 0;
        } catch (Throwable e) {
            throw new IllegalStateException("Could not force release all locks", e);
        }
    }

    private Instant now() {
        return clock.instant();
    }

    private Instant expiresAt(Instant now, Duration duration) {
        if (duration == null) {
            return null;
        }
        return now.plus(duration);
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

    private Connection getInitializedConnection() throws SQLException {
        return sqlTableInitializer.getInitializedConnection();
    }
}
