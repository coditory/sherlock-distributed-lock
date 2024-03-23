package com.coditory.sherlock.samples.mysql.coroutines

import com.coditory.sherlock.sql.BindingMapper.MYSQL_MAPPER
import com.coditory.sherlock.sql.coroutines.SqlSherlock
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object MySqlKtLockSample {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private fun getConnectionFactory(): ConnectionFactory {
        val database = "test"
        val options =
            ConnectionFactoryOptions
                .parse("r2dbc:mysql://localhost:3306/$database")
                .mutate()
                .option(ConnectionFactoryOptions.USER, "mysql")
                .option(ConnectionFactoryOptions.PASSWORD, "mysql")
                .option(ConnectionFactoryOptions.DATABASE, database)
                .build()
        return ConnectionFactories.get(options)
    }

    private suspend fun sample() {
        val sherlock = SqlSherlock.create(getConnectionFactory(), MYSQL_MAPPER)
        val lock = sherlock.createLock("sample-lock")
        lock
            .acquireAndExecute { logger.info("Lock acquired!") }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { sample() }
    }
}
