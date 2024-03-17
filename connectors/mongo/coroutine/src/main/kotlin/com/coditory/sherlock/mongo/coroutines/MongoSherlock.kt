package com.coditory.sherlock.mongo.coroutines

import com.coditory.sherlock.Preconditions.expectNonNull
import com.coditory.sherlock.SherlockDefaults
import com.coditory.sherlock.coroutines.Sherlock
import com.coditory.sherlock.coroutines.SherlockWithConnectorBuilder
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.Document
import java.time.Clock

/**
 * Builds [Sherlock] that uses MongoDB for locking mechanism.
 */
class MongoSherlock private constructor() : SherlockWithConnectorBuilder<MongoSherlock>() {
    private var collection: MongoCollection<Document>? = null
    private var clock = SherlockDefaults.DEFAULT_CLOCK

    /**
     * @param collection mongo collection to be used for locking
     * @return the instance
     */
    fun withLocksCollection(collection: MongoCollection<Document>): MongoSherlock {
        this.collection = expectNonNull(collection, "collection")
        return this
    }

    /**
     * @param clock time provider used in locking mechanism. Default: [SherlockDefaults.DEFAULT_CLOCK]
     * @return the instance
     */
    fun withClock(clock: Clock): MongoSherlock {
        this.clock = expectNonNull(clock, "clock")
        return this
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    override fun build(): Sherlock {
        expectNonNull(collection, "collection")
        val connector = MongoDistributedLockConnector(collection!!, clock)
        return super.build(connector)
    }

    companion object {
        /**
         * @return new instance of the builder
         */
        @JvmStatic
        fun builder(): MongoSherlock {
            return MongoSherlock()
        }

        /**
         * @param collection mongo collection to be used for locking
         * @return new instance of mongo sherlock with default configuration
         */
        @JvmStatic
        fun create(collection: MongoCollection<Document>): Sherlock {
            expectNonNull(collection, "collection")
            return builder()
                .withLocksCollection(collection)
                .build()
        }
    }
}
