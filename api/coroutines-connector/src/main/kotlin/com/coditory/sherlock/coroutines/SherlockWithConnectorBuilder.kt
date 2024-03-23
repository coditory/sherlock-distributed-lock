package com.coditory.sherlock.coroutines

import com.coditory.sherlock.LockDuration
import com.coditory.sherlock.OwnerIdPolicy
import com.coditory.sherlock.Preconditions.expectNonEmpty
import com.coditory.sherlock.Preconditions.expectNonNull
import com.coditory.sherlock.SherlockDefaults
import java.time.Duration

abstract class SherlockWithConnectorBuilder<T : SherlockWithConnectorBuilder<T>> {
    private var duration = SherlockDefaults.DEFAULT_LOCK_DURATION
    private var ownerIdPolicy = OwnerIdPolicy.defaultOwnerIdPolicy()

    /**
     * @param duration how much time a lock should be active. When time passes lock is expired and
     * becomes released. Default: [SherlockDefaults.DEFAULT_LOCK_DURATION]
     * @return the instance
     */
    fun withLockDuration(duration: Duration): T {
        expectNonNull(duration, "duration")
        this.duration = LockDuration.of(duration)
        return instance()
    }

    /**
     * @param ownerId owner id used to specify who can release an acquired lock
     * @return the instance
     */
    fun withOwnerId(ownerId: String): T {
        expectNonEmpty(ownerId, "ownerId")
        ownerIdPolicy = OwnerIdPolicy.staticOwnerId(ownerId)
        return instance()
    }

    fun withOwnerIdPolicy(ownerIdPolicy: OwnerIdPolicy): T {
        this.ownerIdPolicy = ownerIdPolicy
        return instance()
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    abstract fun build(): Sherlock

    protected fun build(connector: SuspendingDistributedLockConnector): Sherlock {
        return SherlockWithConnector(connector, ownerIdPolicy, duration)
    }

    @Suppress("UNCHECKED_CAST")
    private fun instance(): T {
        // builder inheritance
        return this as T
    }
}
