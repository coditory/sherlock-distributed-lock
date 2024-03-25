package com.coditory.sherlock.sql.reactor;

import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.SherlockException;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import com.coditory.sherlock.reactor.DistributedLockConnector;
import com.coditory.sherlock.sql.BindingMapper;
import com.coditory.sherlock.sql.SqlLockQueries;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Statement;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

class SqlDistributedLockConnector implements DistributedLockConnector {
    private final SqlTableInitializer sqlTableInitializer;
    private final SqlLockQueries sqlQueries;
    private final Clock clock;
    private final BindingMapper bindingMapper;

    SqlDistributedLockConnector(
        @NotNull ConnectionFactory connectionFactory,
        @NotNull String tableName,
        @NotNull Clock clock,
        @NotNull BindingMapper bindingMapper
    ) {
        expectNonNull(connectionFactory, "connectionFactory");
        expectNonEmpty(tableName, "tableName");
        expectNonNull(clock, "clock");
        expectNonNull(bindingMapper, "bindingMapper");
        this.clock = clock;
        this.sqlQueries = new SqlLockQueries(tableName, bindingMapper);
        this.sqlTableInitializer = new SqlTableInitializer(connectionFactory, sqlQueries);
        this.bindingMapper = bindingMapper;
    }

    @Override
    @NotNull
    public Mono<InitializationResult> initialize() {
        return sqlTableInitializer.getInitializedConnection()
            .flatMap(c -> Mono.from(c.close()).thenReturn(InitializationResult.of(true)))
            .onErrorMap(e -> new SherlockException("Could not initialize SQL table", e));
    }

    @Override
    @NotNull
    public Mono<AcquireResult> acquire(@NotNull LockRequest lockRequest) {
        Instant now = now();
        return connectionFlatMap(connection ->
            updateReleasedLock(connection, lockRequest, now)
                .flatMap(updated -> updated
                    ? Mono.just(true)
                    : insertLock(connection, lockRequest, now))
        )
            .onErrorMap(e -> new SherlockException("Could not acquire lock: " + lockRequest, e))
            .map(AcquireResult::of);
    }

    @Override
    @NotNull
    public Mono<AcquireResult> acquireOrProlong(@NotNull LockRequest lockRequest) {
        Instant now = now();
        return connectionFlatMap(connection ->
            updateAcquiredOrReleasedLock(connection, lockRequest, now)
                .flatMap(updated -> updated
                    ? Mono.just(true)
                    : insertLock(connection, lockRequest, now))
        )
            .onErrorMap(e -> new SherlockException("Could not acquire or prolong lock: " + lockRequest, e))
            .map(AcquireResult::of);
    }

    @Override
    @NotNull
    public Mono<AcquireResult> forceAcquire(@NotNull LockRequest lockRequest) {
        Instant now = now();
        return connectionFlatMap(connection ->
            updateLockById(connection, lockRequest, now)
                .flatMap(updated -> updated
                    ? Mono.just(true)
                    : insertLock(connection, lockRequest, now))
        )
            .onErrorMap(e -> new SherlockException("Could not force acquire lock: " + lockRequest, e))
            .map(AcquireResult::of);
    }

    private Mono<Boolean> updateReleasedLock(Connection connection, LockRequest lockRequest, Instant now) {
        String lockId = lockRequest.lockId();
        Instant expiresAt = expiresAt(now, lockRequest.duration());
        return statementBinder(connection, sqlQueries.updateReleasedLock())
            .bindOwnerId(lockRequest.ownerId())
            .bindNow(now)
            .bindExpiresAt(expiresAt)
            .bindLockId(lockId)
            .bindNow(now)
            .executeAndGetUpdated()
            .map(updatedRows -> updatedRows > 0);
    }

    private Mono<Boolean> updateAcquiredOrReleasedLock(Connection connection, LockRequest lockRequest, Instant now) {
        String lockId = lockRequest.lockId();
        Instant expiresAt = expiresAt(now, lockRequest.duration());
        return statementBinder(connection, sqlQueries.updateAcquiredOrReleasedLock())
            .bindOwnerId(lockRequest.ownerId())
            .bindNow(now)
            .bindExpiresAt(expiresAt)
            .bindLockId(lockId)
            .bindOwnerId(lockRequest.ownerId())
            .bindNow(now)
            .executeAndGetUpdated()
            .map(updatedRows -> updatedRows > 0);
    }

    private Mono<Boolean> updateLockById(Connection connection, LockRequest lockRequest, Instant now) {
        String lockId = lockRequest.lockId();
        Instant expiresAt = expiresAt(now, lockRequest.duration());
        return statementBinder(connection, sqlQueries.updateLockById())
            .bindOwnerId(lockRequest.ownerId())
            .bindNow(now)
            .bindExpiresAt(expiresAt)
            .bindLockId(lockId)
            .executeAndGetUpdated()
            .map(updatedRows -> updatedRows > 0);
    }

    private Mono<Boolean> insertLock(Connection connection, LockRequest lockRequest, Instant now) {
        String lockId = lockRequest.lockId();
        Instant expiresAt = expiresAt(now, lockRequest.duration());
        return statementBinder(connection, sqlQueries.insertLock())
            .bindLockId(lockId)
            .bindOwnerId(lockRequest.ownerId())
            .bindNow(now)
            .bindExpiresAt(expiresAt)
            .executeAndGetUpdated()
            .map(updatedRows -> updatedRows > 0)
            .onErrorResume(e -> Mono.just(false));
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> release(@NotNull String lockId, @NotNull String ownerId) {
        return statementBinder(sqlQueries.deleteAcquiredByIdAndOwnerId(), binder ->
            binder
                .bindLockId(lockId)
                .bindOwnerId(ownerId)
                .bindNow(now())
                .executeAndGetUpdated()
                .map(updated -> ReleaseResult.of(updated > 0))
        ).onErrorMap(e -> new SherlockException("Could not release lock: " + lockId + ", owner: " + ownerId, e));
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> forceRelease(@NotNull String lockId) {
        return statementBinder(sqlQueries.deleteAcquiredById(), binder ->
            binder
                .bindOwnerId(lockId)
                .bindNow(now())
                .executeAndGetUpdated()
                .map(updated -> ReleaseResult.of(updated > 0))
        ).onErrorMap(e -> new SherlockException("Could not force release lock: " + lockId, e));
    }

    @Override
    @NotNull
    public Mono<ReleaseResult> forceReleaseAll() {
        return connectionFlatMap(connection -> {
            Statement statement = connection.createStatement(sqlQueries.deleteAll());
            return Mono.from(statement.execute())
                .flatMap(result -> Mono.from(result.getRowsUpdated()))
                .map(updated -> ReleaseResult.of(updated > 0));
        })
            .onErrorMap(e -> new SherlockException("Could not force release all locks", e));
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

    private <T> Mono<T> statementBinder(String sql, Function<SqlStatementBinder, Mono<T>> action) {
        return connectionFlatMap(connection -> {
            SqlStatementBinder binder = statementBinder(connection, sql);
            return action.apply(binder);
        });
    }

    private SqlStatementBinder statementBinder(Connection connection, String sql) {
        Statement statement = connection.createStatement(sql);
        return new SqlStatementBinder(statement, bindingMapper);
    }

    private <T> Mono<T> connectionFlatMap(Function<Connection, Mono<T>> action) {
        return sqlTableInitializer.getInitializedConnection()
            .flatMap(connection ->
                action.apply(connection)
                    .flatMap(v -> Mono.from(connection.close()).thenReturn(v))
                    .onErrorResume(e -> Mono.from(connection.close()).then(Mono.error(e)))
                    .switchIfEmpty(Mono.fromCallable(connection::close).flatMap(Mono::from).then(Mono.empty()))
            );
    }
}
