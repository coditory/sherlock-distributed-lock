package com.coditory.sherlock.inmem.coroutines

import com.coditory.sherlock.Preconditions.expectNonNull
import com.coditory.sherlock.SherlockDefaults
import com.coditory.sherlock.coroutines.Sherlock
import com.coditory.sherlock.coroutines.SherlockWithConnectorBuilder
import com.coditory.sherlock.inmem.InMemoryDistributedLockStorage
import java.time.Clock

/**
 * Builds [Sherlock] that that stores locks in memory.
 *
 * Designed for testing purposes only.
 */
class InMemorySherlock private constructor() : SherlockWithConnectorBuilder<InMemorySherlock>() {
    private var clock = SherlockDefaults.DEFAULT_CLOCK
    private var storage = InMemoryDistributedLockStorage()

    /**
     * @param clock time provider used in locking mechanism. Default: [SherlockDefaults.DEFAULT_CLOCK]
     * @return the instance
     */
    fun withClock(clock: Clock): InMemorySherlock {
        this.clock = expectNonNull(clock, "clock")
        return this
    }

    /**
     * Use shared stage for all instances of [Sherlock].
     *
     * @return the instance
     */
    fun withSharedStorage(): InMemorySherlock {
        storage = InMemoryDistributedLockStorage.singleton()
        return this
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    override fun build(): Sherlock {
        val connector =
            KtInMemoryDistributedLockConnector(
                clock,
                storage,
            )
        return super.build(connector)
    }

    companion object {
        /**
         * @return new instance of the builder
         */
        @JvmStatic
        fun builder(): InMemorySherlock {
            return InMemorySherlock()
        }

        /**
         * @return new instance iof in memory sherlock with default configuration
         */
        @JvmStatic
        fun create(): Sherlock {
            return builder().build()
        }
    }
}
