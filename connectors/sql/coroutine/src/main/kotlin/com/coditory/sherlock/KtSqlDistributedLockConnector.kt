package com.coditory.sherlock

import io.r2dbc.spi.Connection
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import java.time.Clock
import java.time.Instant

internal class KtSqlDistributedLockConnector(
    connectionFactory: ConnectionFactory,
    tableName: String,
    private val bindingMapper: BindingMapper,
    private val clock: Clock,
) : KtDistributedLockConnector {
    private val sqlQueries = SqlLockQueries(tableName, bindingMapper)
    private val sqlTableInitializer = KtSqlTableInitializer(connectionFactory, sqlQueries)

    override suspend fun initialize() {
        try {
            val connection = getInitializedConnection()
            connection.close()
        } catch (e: Throwable) {
            throw SherlockException("Could not initialize SQL table", e)
        }
    }

    override suspend fun acquire(lockRequest: LockRequest): Boolean {
        val now = now()
        return try {
            getInitializedConnection().use { connection ->
                updateReleasedLock(connection, lockRequest, now) ||
                    insertLock(connection, lockRequest, now)
            }
        } catch (e: Throwable) {
            throw SherlockException("Could not acquire lock: $lockRequest", e)
        }
    }

    override suspend fun acquireOrProlong(lockRequest: LockRequest): Boolean {
        val now = now()
        return try {
            getInitializedConnection().use { connection ->
                updateAcquiredOrReleasedLock(connection, lockRequest, now) ||
                    insertLock(connection, lockRequest, now)
            }
        } catch (e: Throwable) {
            throw SherlockException("Could not acquire or prolong lock: $lockRequest", e)
        }
    }

    override suspend fun forceAcquire(lockRequest: LockRequest): Boolean {
        val now = now()
        return try {
            getInitializedConnection().use { connection ->
                updateLockById(connection, lockRequest, now) ||
                    insertLock(connection, lockRequest, now)
            }
        } catch (e: Throwable) {
            throw SherlockException("Could not force acquire lock: $lockRequest", e)
        }
    }

    private suspend fun updateReleasedLock(
        connection: Connection,
        lockRequest: LockRequest,
        now: Instant,
    ): Boolean {
        val lockId = lockRequest.lockId.value
        val expiresAt = expiresAt(now, lockRequest.duration)
        return statementBinder(connection, sqlQueries.updateReleasedLock())
            .bindOwnerId(lockRequest.ownerId.value)
            .bindNow(now)
            .bindExpiresAt(expiresAt)
            .bindLockId(lockId)
            .bindNow(now)
            .executeAndGetUpdated()
            .let { it > 0 }
    }

    private suspend fun updateAcquiredOrReleasedLock(
        connection: Connection,
        lockRequest: LockRequest,
        now: Instant,
    ): Boolean {
        val lockId = lockRequest.lockId.value
        val expiresAt = expiresAt(now, lockRequest.duration)
        return statementBinder(connection, sqlQueries.updateAcquiredOrReleasedLock())
            .bindOwnerId(lockRequest.ownerId.value)
            .bindNow(now)
            .bindExpiresAt(expiresAt)
            .bindLockId(lockId)
            .bindOwnerId(lockRequest.ownerId.value)
            .bindNow(now)
            .executeAndGetUpdated()
            .let { it > 0 }
    }

    private suspend fun updateLockById(
        connection: Connection,
        lockRequest: LockRequest,
        now: Instant,
    ): Boolean {
        val lockId = lockRequest.lockId.value
        val expiresAt = expiresAt(now, lockRequest.duration)
        return statementBinder(connection, sqlQueries.updateLockById())
            .bindOwnerId(lockRequest.ownerId.value)
            .bindNow(now)
            .bindExpiresAt(expiresAt)
            .bindLockId(lockId)
            .executeAndGetUpdated()
            .let { it > 0 }
    }

    private suspend fun insertLock(
        connection: Connection,
        lockRequest: LockRequest,
        now: Instant,
    ): Boolean {
        val lockId = lockRequest.lockId.value
        val expiresAt = expiresAt(now, lockRequest.duration)
        return try {
            statementBinder(connection, sqlQueries.insertLock())
                .bindLockId(lockId)
                .bindOwnerId(lockRequest.ownerId.value)
                .bindNow(now)
                .bindExpiresAt(expiresAt)
                .executeAndGetUpdated()
                .let { it > 0 }
        } catch (e: Throwable) {
            false
        }
    }

    override suspend fun release(
        lockId: LockId,
        ownerId: OwnerId,
    ): Boolean {
        return try {
            statementBinder(sqlQueries.deleteAcquiredByIdAndOwnerId()) { binder ->
                binder
                    .bindLockId(lockId.value)
                    .bindOwnerId(ownerId.value)
                    .bindNow(now())
                    .executeAndGetUpdated()
                    .let { it > 0 }
            }
        } catch (e: Throwable) {
            throw SherlockException("Could not release lock: " + lockId.value + ", owner: " + ownerId, e)
        }
    }

    override suspend fun forceRelease(lockId: LockId): Boolean {
        return try {
            statementBinder(sqlQueries.deleteAcquiredById()) { binder ->
                binder
                    .bindOwnerId(lockId.value)
                    .bindNow(now())
                    .executeAndGetUpdated()
                    .let { it > 0 }
            }
        } catch (e: Throwable) {
            throw SherlockException("Could not force release lock: " + lockId.value, e)
        }
    }

    override suspend fun forceReleaseAll(): Boolean {
        try {
            getInitializedConnection().use { connection ->
                val statement = connection.createStatement(sqlQueries.deleteAll())
                val result = statement.execute().awaitFirst()
                val updatedRows = result.rowsUpdated.awaitFirst()
                return updatedRows > 0
            }
        } catch (e: Throwable) {
            throw SherlockException("Could not force release all locks", e)
        }
    }

    private suspend fun <R> statementBinder(
        sql: String,
        block: suspend (KtSqlStatementBinder) -> R,
    ): R {
        return getInitializedConnection().use { connection ->
            val statement = connection.createStatement(sql)
            val statementBinder = KtSqlStatementBinder(statement, bindingMapper)
            block.invoke(statementBinder)
        }
    }

    private fun statementBinder(
        connection: Connection,
        sql: String,
    ): KtSqlStatementBinder {
        val statement = connection.createStatement(sql)
        return KtSqlStatementBinder(statement, bindingMapper)
    }

    private fun expiresAt(
        now: Instant,
        duration: LockDuration?,
    ): Instant? {
        return if (duration == null || duration.value == null) {
            null
        } else {
            now.plus(duration.value)
        }
    }

    private fun now(): Instant {
        return clock.instant()
    }

    private suspend fun getInitializedConnection(): Connection {
        return sqlTableInitializer.getInitializedConnection()
    }
}
