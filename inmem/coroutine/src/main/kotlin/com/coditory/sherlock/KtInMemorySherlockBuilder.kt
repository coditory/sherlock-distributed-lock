package com.coditory.sherlock

import java.time.Clock

/**
 * Builds [KtSherlock] that that stores locks in memory.
 *
 * Designed for testing purposes only.
 */
class KtInMemorySherlockBuilder private constructor() : KtSherlockWithConnectorBuilder<KtInMemorySherlockBuilder>() {
    private var clock = SherlockDefaults.DEFAULT_CLOCK
    private var storage = InMemoryDistributedLockStorage()

    /**
     * @param clock time provider used in locking mechanism. Default: [SherlockDefaults.DEFAULT_CLOCK]
     * @return the instance
     */
    fun withClock(clock: Clock): KtInMemorySherlockBuilder {
        this.clock = Preconditions.expectNonNull(clock, "clock")
        return this
    }

    /**
     * Use shared stage for all instances of [KtSherlock].
     *
     * @return the instance
     */
    fun withSharedStorage(): KtInMemorySherlockBuilder {
        storage = InMemoryDistributedLockStorage.singleton()
        return this
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    override fun build(): KtSherlock {
        val connector = KtInMemoryDistributedLockConnector(
            clock, storage
        )
        return super.build(connector)
    }

    companion object {
        /**
         * @return new instance of the builder
         */
        @JvmStatic
        fun coroutineInMemorySherlockBuilder(): KtInMemorySherlockBuilder {
            return KtInMemorySherlockBuilder()
        }

        /**
         * @return new instance iof in memory sherlock with default configuration
         */
        @JvmStatic
        fun coroutineInMemorySherlock(): KtSherlock {
            return coroutineInMemorySherlockBuilder().build()
        }
    }
}
