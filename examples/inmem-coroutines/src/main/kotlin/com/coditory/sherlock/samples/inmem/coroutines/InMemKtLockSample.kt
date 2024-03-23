package com.coditory.sherlock.samples.inmem.coroutines

import com.coditory.sherlock.inmem.coroutines.InMemorySherlock
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object InMemKtLockSample {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private suspend fun sample() {
        val sherlock = InMemorySherlock.create()
        val lock = sherlock.createLock("sample-lock")
        lock.acquireAndExecute {
            logger.info("Lock acquired!")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking { sample() }
    }
}
