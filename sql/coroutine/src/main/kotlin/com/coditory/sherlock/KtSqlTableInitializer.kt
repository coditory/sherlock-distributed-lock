package com.coditory.sherlock

import io.r2dbc.spi.Connection
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

internal class KtSqlTableInitializer(
    private val connectionFactory: ConnectionFactory,
    private val sqlQueries: SqlLockQueries
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val initialized = AtomicBoolean(false)

    suspend fun getInitializedConnection(): Connection {
        return if (initialized.compareAndSet(false, true))
            initialize()
        else
            createConnectionWithRetry()
    }

    private suspend fun initialize(): Connection {
        val connection = createConnectionWithRetry()
        return try {
            createTable(connection)
            createIndex(connection)
            connection
        } catch (e: Throwable) {
            close(connection)
            throw e
        }
    }

    private suspend fun createTable(connection: Connection) {
        val createTableStatement = connection.createStatement(sqlQueries.createLocksTable())
        try {
            createTableStatement.execute().awaitFirst()
            connection.commitTransaction().awaitFirstOrNull()
        } catch (e: Throwable) {
            val checkTableStatement = connection.createStatement(sqlQueries.checkTableExits())
            checkTableStatement.execute().awaitFirst()
        } catch (e: Throwable) {
            initialized.set(false)
            throw SherlockException("Could not initialize locks table", e)
        }
    }

    private suspend fun createIndex(connection: Connection) {
        val createIndexStatement = connection.createStatement(sqlQueries.createLocksIndex())
        try {
            createIndexStatement.execute().awaitFirst()
            connection.commitTransaction().awaitFirstOrNull()
        } catch (e: Throwable) {
            initialized.set(false)
            throw SherlockException("Could not initialize locks table index", e)
        }
    }

    private suspend fun createConnectionWithRetry(): Connection {
        // Retrying connection because of a bug in connection pool
        // https://github.com/r2dbc/r2dbc-pool/issues/164
        // it seems that retrying connection once solves the issue
        return try {
            connectionFactory.create().awaitFirst()
        } catch (e: Throwable) {
            logger.debug("Could not create connection. Retrying one more time", e)
            connectionFactory.create().awaitFirst()
        }
    }

    private suspend fun close(connection: Connection) {
        try {
            connection.close().awaitFirstOrNull()
        } catch (e: Throwable) {
            logger.warn("Could not close connection", e)
        }
    }
}
