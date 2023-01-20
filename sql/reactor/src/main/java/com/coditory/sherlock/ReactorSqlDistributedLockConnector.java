package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Statement;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.util.function.Function;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class ReactorSqlDistributedLockConnector implements ReactorDistributedLockConnector {
    private final ReactorSqlTableInitializer sqlTableInitializer;
    private final SqlLockQueries sqlQueries;
    private final Clock clock;
    private final BindingParameterMapper bindingParameterMapper;

    ReactorSqlDistributedLockConnector(
            ConnectionFactory connectionFactory, String tableName, Clock clock, BindingParameterMapper bindingParameterMapper) {
        this.clock = expectNonNull(clock, "Expected non null clock");
        this.sqlQueries = new SqlLockQueries(tableName, bindingParameterMapper);
        this.sqlTableInitializer = new ReactorSqlTableInitializer(connectionFactory, sqlQueries);
        this.bindingParameterMapper = bindingParameterMapper;
    }

    @Override
    public Mono<InitializationResult> initialize() {
        return sqlTableInitializer.getInitializedConnection()
                .flatMap(c -> Mono.from(c.close()).thenReturn(InitializationResult.of(true)))
                .onErrorMap(e -> new SherlockException("Could not initialize SQL table", e));
    }

    @Override
    public Mono<AcquireResult> acquire(LockRequest lockRequest) {
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
    public Mono<AcquireResult> acquireOrProlong(LockRequest lockRequest) {
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
    public Mono<AcquireResult> forceAcquire(LockRequest lockRequest) {
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
        String lockId = lockRequest.getLockId().getValue();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        return statementBinder(connection, sqlQueries.updateReleasedLock())
                .bindOwnerId(lockRequest.getOwnerId().getValue())
                .bindNow(now)
                .bindExpiresAt(expiresAt)
                .bindLockId(lockId)
                .bindNow(now)
                .executeAndGetUpdated()
                .map(updatedRows -> updatedRows > 0);
    }

    private Mono<Boolean> updateAcquiredOrReleasedLock(Connection connection, LockRequest lockRequest, Instant now) {
        String lockId = lockRequest.getLockId().getValue();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        return statementBinder(connection, sqlQueries.updateAcquiredOrReleasedLock())
                .bindOwnerId(lockRequest.getOwnerId().getValue())
                .bindNow(now)
                .bindExpiresAt(expiresAt)
                .bindLockId(lockId)
                .bindOwnerId(lockRequest.getOwnerId().getValue())
                .bindNow(now)
                .executeAndGetUpdated()
                .map(updatedRows -> updatedRows > 0);
    }

    private Mono<Boolean> updateLockById(Connection connection, LockRequest lockRequest, Instant now) {
        String lockId = lockRequest.getLockId().getValue();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        return statementBinder(connection, sqlQueries.updateLockById())
                .bindOwnerId(lockRequest.getOwnerId().getValue())
                .bindNow(now)
                .bindExpiresAt(expiresAt)
                .bindLockId(lockId)
                .executeAndGetUpdated()
                .map(updatedRows -> updatedRows > 0);
    }

    private Mono<Boolean> insertLock(Connection connection, LockRequest lockRequest, Instant now) {
        String lockId = lockRequest.getLockId().getValue();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        return statementBinder(connection, sqlQueries.insertLock())
                .bindLockId(lockId)
                .bindOwnerId(lockRequest.getOwnerId().getValue())
                .bindNow(now)
                .bindExpiresAt(expiresAt)
                .executeAndGetUpdated()
                .map(updatedRows -> updatedRows > 0)
                .onErrorResume(e -> {
                    // TODO: check does it work!?!?
                    if (e instanceof SQLException && !e.getMessage().toLowerCase().contains("duplicate")) {
                        Mono.error(e);
                    }
                    return Mono.just(false);
                });
    }

    @Override
    public Mono<ReleaseResult> release(LockId lockId, OwnerId ownerId) {
        return statementBinder(sqlQueries.deleteAcquiredByIdAndOwnerId(), binder ->
                binder
                        .bindLockId(lockId.getValue())
                        .bindOwnerId(ownerId.getValue())
                        .bindNow(now())
                        .executeAndGetUpdated()
                        .map(updated -> ReleaseResult.of(updated > 0))
        ).onErrorMap(e -> new SherlockException("Could not release lock: " + lockId.getValue() + ", owner: " + ownerId, e));
    }

    @Override
    public Mono<ReleaseResult> forceRelease(LockId lockId) {
        return statementBinder(sqlQueries.deleteAcquiredById(), binder ->
                binder
                        .bindOwnerId(lockId.getValue())
                        .bindNow(now())
                        .executeAndGetUpdated()
                        .map(updated -> ReleaseResult.of(updated > 0))
        ).onErrorMap(e -> new SherlockException("Could not force release lock: " + lockId.getValue(), e));
    }

    @Override
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

    private Instant expiresAt(Instant now, LockDuration duration) {
        if (duration == null || duration.getValue() == null) {
            return null;
        }
        return now.plus(duration.getValue());
    }

    private <T> Mono<T> statementBinder(String sql, Function<StatementBinder, Mono<T>> action) {
        return connectionFlatMap(connection -> {
            StatementBinder binder = statementBinder(connection, sql);
            return action.apply(binder);
        });
    }

    private StatementBinder statementBinder(Connection connection, String sql) {
        Statement statement = connection.createStatement(sql);
        return new StatementBinder(statement, bindingParameterMapper);
    }

    private <T> Mono<T> connectionFlatMap(Function<Connection, Mono<T>> action) {
        return sqlTableInitializer.getInitializedConnection()
                .flatMap(connection ->
                        action.apply(connection)
                                // .flatMap(v -> Mono.from(connection.commitTransaction()).thenReturn(v))
                                .flatMap(v -> Mono.from(connection.close()).thenReturn(v))
                                .onErrorResume(e -> Mono.from(connection.close()).then(Mono.error(e)))
                                .switchIfEmpty(Mono.fromCallable(connection::close).flatMap(Mono::from).then(Mono.empty()))
                );
    }
}
