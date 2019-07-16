package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.OwnerIdGenerator;

import java.time.Duration;

import static com.coditory.sherlock.common.OwnerIdGenerator.staticOwnerIdGenerator;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_OWNER_ID_GENERATOR;

abstract class ReactiveSherlockWithConnectorBuilder<T extends ReactiveSherlockWithConnectorBuilder> {
  private LockDuration duration = DEFAULT_LOCK_DURATION;
  private OwnerIdGenerator ownerIdGenerator = DEFAULT_OWNER_ID_GENERATOR;

  /**
   * @return sherlock instance
   * @throws IllegalArgumentException when some required values are missing
   */
  public abstract ReactiveSherlock build();

  /**
   * @return sherlock instance
   * @throws IllegalArgumentException when some required values are missing
   */
  public <A> A build(ReactiveSherlockApiWrapper<A> apiWrapper) {
    return apiWrapper.wrapApi(build());
  }

  /**
   * @param duration how much time a lock should be active. When time passes lock is expired and
   *     becomes released. Default: {@link com.coditory.sherlock.common.SherlockDefaults#DEFAULT_LOCK_DURATION}
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
    this.ownerIdGenerator = staticOwnerIdGenerator(ownerId);
    return instance();
  }

  /**
   * Generates random owner id for every instance of lock object.
   *
   * @return the instance
   * @see this#withOwnerId(String)
   */
  public T withRandomOwnerId() {
    this.ownerIdGenerator = OwnerIdGenerator.RANDOM_OWNER_ID_GENERATOR;
    return instance();
  }

  /**
   * Generates random owner id once per JVM (as a static field). Such a strategy ensures that all
   * locks of the same process has the same owner id.
   *
   * @return the instance
   * @see this#withOwnerId(String)
   */
  public T withRandomStaticOwnerId() {
    this.ownerIdGenerator = OwnerIdGenerator.RANDOM_STATIC_OWNER_ID_GENERATOR;
    return instance();
  }

  protected ReactiveSherlock build(ReactiveDistributedLockConnector connector) {
    return new ReactiveSherlockWithConnector(connector, ownerIdGenerator, duration);
  }

  @SuppressWarnings("unchecked")
  private T instance() {
    // builder inheritance
    return (T) this;
  }
}

