package com.coditory.sherlock.sql.coroutines

import com.coditory.sherlock.LockRequest
import com.coditory.sherlock.SherlockException
import com.coditory.sherlock.coroutines.SuspendingDistributedLockConnector
import com.coditory.sherlock.sql.BindingMapper
import com.coditory.sherlock.sql.SqlLockQueries
import io.r2dbc.spi.Connection
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import java.time.Clock
import java.time.Duration
import java.time.Instant

internal class SqlDistributedLockConnector(
    connectionFactory: ConnectionFactory,
    tableName: String,
    private val bindingMapper: BindingMapper,
    private val clock: Clock,
) : SuspendingDistributedLockConnector {
    private val sqlQueries = SqlLockQueries(tableName, bindingMapper)
    private val sqlTableInitializer = SqlTableInitializer(connectionFactory, sqlQueries)

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
        val lockId = lockRequest.lockId
        val expiresAt = expiresAt(now, lockRequest.duration)
        return statementBinder(connection, sqlQueries.updateReleasedLock())
            .bindOwnerId(lockRequest.ownerId)
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
        val lockId = lockRequest.lockId
        val expiresAt = expiresAt(now, lockRequest.duration)
        return statementBinder(connection, sqlQueries.updateAcquiredOrReleasedLock())
            .bindOwnerId(lockRequest.ownerId)
            .bindNow(now)
            .bindExpiresAt(expiresAt)
            .bindLockId(lockId)
            .bindOwnerId(lockRequest.ownerId)
            .bindNow(now)
            .executeAndGetUpdated()
            .let { it > 0 }
    }

    private suspend fun updateLockById(
        connection: Connection,
        lockRequest: LockRequest,
        now: Instant,
    ): Boolean {
        val lockId = lockRequest.lockId
        val expiresAt = expiresAt(now, lockRequest.duration)
        return statementBinder(connection, sqlQueries.updateLockById())
            .bindOwnerId(lockRequest.ownerId)
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
        val lockId = lockRequest.lockId
        val expiresAt = expiresAt(now, lockRequest.duration)
        return try {
            statementBinder(connection, sqlQueries.insertLock())
                .bindLockId(lockId)
                .bindOwnerId(lockRequest.ownerId)
                .bindNow(now)
                .bindExpiresAt(expiresAt)
                .executeAndGetUpdated()
                .let { it > 0 }
        } catch (e: Throwable) {
            false
        }
    }

    override suspend fun release(lockId: String, ownerId: String): Boolean {
        return try {
            statementBinder(sqlQueries.deleteAcquiredByIdAndOwnerId()) { binder ->
                binder
                    .bindLockId(lockId)
                    .bindOwnerId(ownerId)
                    .bindNow(now())
                    .executeAndGetUpdated()
                    .let { it > 0 }
            }
        } catch (e: Throwable) {
            throw SherlockException("Could not release lock: $lockId, owner: $ownerId", e)
        }
    }

    override suspend fun forceRelease(lockId: String): Boolean {
        return try {
            statementBinder(sqlQueries.deleteAcquiredById()) { binder ->
                binder
                    .bindOwnerId(lockId)
                    .bindNow(now())
                    .executeAndGetUpdated()
                    .let { it > 0 }
            }
        } catch (e: Throwable) {
            throw SherlockException("Could not force release lock: $lockId", e)
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
        block: suspend (SqlStatementBinder) -> R,
    ): R {
        return getInitializedConnection().use { connection ->
            val statement = connection.createStatement(sql)
            val statementBinder = SqlStatementBinder(statement, bindingMapper)
            block.invoke(statementBinder)
        }
    }

    private fun statementBinder(
        connection: Connection,
        sql: String,
    ): SqlStatementBinder {
        val statement = connection.createStatement(sql)
        return SqlStatementBinder(statement, bindingMapper)
    }

    private fun expiresAt(now: Instant, duration: Duration?): Instant? {
        return if (duration == null) {
            null
        } else {
            now.plus(duration)
        }
    }

    private fun now(): Instant {
        return clock.instant()
    }

    private suspend fun getInitializedConnection(): Connection {
        return sqlTableInitializer.getInitializedConnection()
    }
}
