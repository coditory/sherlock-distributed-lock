package com.coditory.sherlock.samples.postgres

import com.coditory.sherlock.sql.BindingMapper
import com.coditory.sherlock.sql.coroutines.SqlSherlock
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Duration

object PostgresKtLockSample {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val sherlock =
        SqlSherlock.builder()
            .withClock(Clock.systemUTC())
            .withLockDuration(Duration.ofMinutes(5))
            .withUniqueOwnerId()
            .withConnectionFactory(getConnectionFactory())
            .withBindingMapper(BindingMapper.POSTGRES_MAPPER)
            .withLocksTable("LOCKS")
            .build()

    private fun getConnectionFactory(): ConnectionFactory {
        val database = "test"
        val options =
            ConnectionFactoryOptions
                .parse("r2dbc:postgresql://localhost:5432/$database")
                .mutate()
                .option(ConnectionFactoryOptions.USER, "postgres")
                .option(ConnectionFactoryOptions.PASSWORD, "postgres")
                .option(ConnectionFactoryOptions.DATABASE, database)
                .build()
        return ConnectionFactories.get(options)
    }

    private suspend fun sample() {
        val lock = sherlock.createLock("sample-lock")
        lock
            .acquireAndExecute { logger.info("Lock acquired!") }
    }

    fun main() {
        runBlocking { sample() }
    }
}
