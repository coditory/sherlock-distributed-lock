package com.coditory.sherlock.mongo.coroutines

import com.coditory.sherlock.Preconditions.expectNonNull
import com.coditory.sherlock.SherlockDefaults
import com.coditory.sherlock.coroutines.KtSherlock
import com.coditory.sherlock.coroutines.KtSherlockWithConnectorBuilder
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.Document
import java.time.Clock

/**
 * Builds [KtSherlock] that uses MongoDB for locking mechanism.
 */
class KtMongoSherlockBuilder private constructor() : KtSherlockWithConnectorBuilder<KtMongoSherlockBuilder>() {
    private var collection: MongoCollection<Document>? = null
    private var clock = SherlockDefaults.DEFAULT_CLOCK

    /**
     * @param collection mongo collection to be used for locking
     * @return the instance
     */
    fun withLocksCollection(collection: MongoCollection<Document>): KtMongoSherlockBuilder {
        this.collection = expectNonNull(collection, "collection")
        return this
    }

    /**
     * @param clock time provider used in locking mechanism. Default: [SherlockDefaults.DEFAULT_CLOCK]
     * @return the instance
     */
    fun withClock(clock: Clock): KtMongoSherlockBuilder {
        this.clock = expectNonNull(clock, "clock")
        return this
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    override fun build(): KtSherlock {
        expectNonNull(collection, "collection")
        val connector = KtMongoDistributedLockConnector(collection!!, clock)
        return super.build(connector)
    }

    companion object {
        /**
         * @return new instance of the builder
         */
        @JvmStatic
        fun coroutineMongoSherlock(): KtMongoSherlockBuilder {
            return KtMongoSherlockBuilder()
        }

        /**
         * @param collection mongo collection to be used for locking
         * @return new instance of mongo sherlock with default configuration
         */
        @JvmStatic
        fun coroutineMongoSherlock(collection: MongoCollection<Document>): KtSherlock {
            expectNonNull(collection, "collection")
            return coroutineMongoSherlock()
                .withLocksCollection(collection)
                .build()
        }
    }
}
