package com.coditory.sherlock.samples.postgres.coroutines

import com.coditory.sherlock.coroutines.migrator.SherlockMigrator
import com.coditory.sherlock.migrator.ChangeSet
import com.coditory.sherlock.sql.BindingMapper
import com.coditory.sherlock.sql.coroutines.SqlSherlock
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object PostgresKtAnnotatedMigrationSample {
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
            .addAnnotatedChangeSets(AnnotatedMigration())
            .migrate()
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
            .addAnnotatedChangeSets(AnnotatedMigration2())
            .migrate()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { sample() }
    }

    class AnnotatedMigration {
        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

        @ChangeSet(order = 0, id = "change-set-a")
        fun changeSetA() {
            logger.info("Annotated change-set: A")
        }

        @ChangeSet(order = 1, id = "change-set-b")
        fun changeSetB() {
            logger.info("Annotated change-set: B")
        }
    }

    class AnnotatedMigration2 {
        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

        @ChangeSet(order = 0, id = "change-set-a")
        fun changeSetA() {
            logger.info("Annotated change-set: A")
        }

        @ChangeSet(order = 1, id = "change-set-b")
        fun changeSetB() {
            logger.info("Annotated change-set: B")
        }

        @ChangeSet(order = 2, id = "change-set-c")
        fun changeSetC() {
            logger.info("Annotated change-set: C")
        }
    }
}
