package com.coditory.sherlock.sample.inmem

import com.coditory.sherlock.KtInMemorySherlockBuilder
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock

object InMemKtSample {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private suspend fun sampleInMemLockUsage() {
        val sherlock =
            KtInMemorySherlockBuilder.coroutineInMemorySherlockBuilder()
                .withClock(Clock.systemUTC())
                .withUniqueOwnerId()
                .withSharedStorage()
                .build()
        // ...or short equivalent:
        // val sherlockWithDefaults = coroutineInMemorySherlock()
        val lock = sherlock.createLock("sample-lock")
        lock.acquireAndExecute {
            logger.info("Lock acquired!")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            sampleInMemLockUsage()
        }
    }
}
