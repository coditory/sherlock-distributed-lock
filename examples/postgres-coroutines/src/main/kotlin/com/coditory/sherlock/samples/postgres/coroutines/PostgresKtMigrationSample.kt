package com.coditory.sherlock.samples.postgres.coroutines

import com.coditory.sherlock.coroutines.migrator.SherlockMigrator
import com.coditory.sherlock.sql.BindingMapper
import com.coditory.sherlock.sql.coroutines.SqlSherlock
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object PostgresKtMigrationSample {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

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
        val sherlock = SqlSherlock.create(getConnectionFactory(), BindingMapper.POSTGRES_MAPPER)
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
            .addChangeSet("change-set-1") { logger.info("Change-set 1") }
            .addChangeSet("change-set-2") { logger.info("Change-set 2") }
            .migrate()
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
            .addChangeSet("change-set-1") { logger.info("Change-set 1") }
            .addChangeSet("change-set-2") { logger.info("Change-set 2") }
            .addChangeSet("change-set-3") { logger.info("Change-set 3") }
            .migrate()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { sample() }
    }
}
