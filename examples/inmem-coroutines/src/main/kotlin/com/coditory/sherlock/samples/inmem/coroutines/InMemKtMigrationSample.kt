package com.coditory.sherlock.samples.inmem.coroutines

import com.coditory.sherlock.coroutines.migrator.SherlockMigrator
import com.coditory.sherlock.inmem.coroutines.InMemorySherlock
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object InMemKtMigrationSample {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private suspend fun sample() {
        val sherlock = InMemorySherlock.create()
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
