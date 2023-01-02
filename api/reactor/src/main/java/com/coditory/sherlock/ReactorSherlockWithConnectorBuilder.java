package com.coditory.sherlock;

import java.time.Duration;

import static com.coditory.sherlock.OwnerIdPolicy.*;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_OWNER_ID_POLICY;

abstract class ReactorSherlockWithConnectorBuilder<T extends ReactorSherlockWithConnectorBuilder<?>> {
    private LockDuration duration = DEFAULT_LOCK_DURATION;
    private OwnerIdPolicy ownerIdPolicy = DEFAULT_OWNER_ID_POLICY;

    /**
     * @param duration how much time a lock should be active. When time passes lock is expired and
     *                 becomes released. Default: {@link SherlockDefaults#DEFAULT_LOCK_DURATION}
     * @return the instance
     */
    public T withLockDuration(Duration duration) {
        this.duration = LockDuration.of(duration);
        return instance();
    }

    /**
     * @param ownerId owner id used to specify who can release an acquired lock
     * @return the instance
     */
    public T withOwnerId(String ownerId) {
        this.ownerIdPolicy = staticOwnerIdPolicy(ownerId);
        return instance();
    }

    /**
     * Generates random unique owner id for every instance of lock object.
     *
     * @return the instance
     * @see this#withOwnerId(String)
     */
    public T withUniqueOwnerId() {
        this.ownerIdPolicy = uniqueOwnerIdPolicy();
        return instance();
    }

    /**
     * Generates random owner id once per JVM (as a static field). Such a strategy ensures that all
     * locks of the same process has the same owner id.
     *
     * @return the instance
     * @see this#withOwnerId(String)
     */
    public T withStaticUniqueOwnerId() {
        this.ownerIdPolicy = staticUniqueOwnerIdPolicy();
        return instance();
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    public abstract ReactorSherlock build();

    protected ReactorSherlock build(ReactorDistributedLockConnector connector) {
        return new ReactorSherlockWithConnector(connector, ownerIdPolicy, duration);
    }

    @SuppressWarnings("unchecked")
    private T instance() {
        // builder inheritance
        return (T) this;
    }
}

