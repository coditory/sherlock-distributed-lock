package com.coditory.sherlock.coroutines

import com.coditory.sherlock.LockDuration
import com.coditory.sherlock.OwnerIdPolicy
import com.coditory.sherlock.Preconditions
import com.coditory.sherlock.SherlockDefaults
import java.time.Duration

abstract class KtSherlockWithConnectorBuilder<T : KtSherlockWithConnectorBuilder<T>> {
    private var duration = SherlockDefaults.DEFAULT_LOCK_DURATION
    private var ownerIdPolicy = SherlockDefaults.DEFAULT_OWNER_ID_POLICY

    /**
     * @param duration how much time a lock should be active. When time passes lock is expired and
     * becomes released. Default: [SherlockDefaults.DEFAULT_LOCK_DURATION]
     * @return the instance
     */
    fun withLockDuration(duration: Duration): T {
        Preconditions.expectNonNull(duration, "duration")
        this.duration = LockDuration.of(duration)
        return instance()
    }

    /**
     * @param ownerId owner id used to specify who can release an acquired lock
     * @return the instance
     */
    fun withOwnerId(ownerId: String): T {
        Preconditions.expectNonEmpty(ownerId, "ownerId")
        ownerIdPolicy = OwnerIdPolicy.staticOwnerIdPolicy(ownerId)
        return instance()
    }

    /**
     * Generates random unique owner id for every instance of lock object.
     *
     * @return the instance
     * @see withOwnerId
     */
    fun withUniqueOwnerId(): T {
        ownerIdPolicy = OwnerIdPolicy.uniqueOwnerIdPolicy()
        return instance()
    }

    /**
     * Generates random owner id once per JVM (as a static field). Such a strategy ensures that all
     * locks of the same process has the same owner id.
     *
     * @return the instance
     * @see withOwnerId
     */
    fun withStaticUniqueOwnerId(): T {
        ownerIdPolicy = OwnerIdPolicy.staticUniqueOwnerIdPolicy()
        return instance()
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    abstract fun build(): KtSherlock

    protected fun build(connector: KtDistributedLockConnector): KtSherlock {
        return KtSherlockWithConnector(connector, ownerIdPolicy, duration)
    }

    @Suppress("UNCHECKED_CAST")
    private fun instance(): T {
        // builder inheritance
        return this as T
    }
}
