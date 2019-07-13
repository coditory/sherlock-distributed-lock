package com.coditory.sherlock.reactor.test;

import com.coditory.sherlock.common.InstanceId;
import com.coditory.sherlock.reactor.ReactorDistributedLock;
import com.coditory.sherlock.reactor.ReactorSherlock;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.reactor.test.ReactorDistributedLockMock.singleStateLock;

/**
 * Use to stub {@link ReactorSherlock} in tests.
 */
public final class ReactorSherlockStub implements ReactorSherlock {
  private final Map<String, ReactorDistributedLock> locksById = new HashMap<>();
  private InstanceId instanceId = InstanceId.of("tested-instance");
  private Duration duration = DEFAULT_LOCK_DURATION;
  private boolean defaultLockResult = true;

  /**
   * Make the stub produce released locks by default
   */
  static public ReactorSherlockStub withReleasedLocks() {
    return new ReactorSherlockStub()
        .withDefaultAcquireResult(true);
  }

  /**
   * Make the stub produce acquired locks by default
   */
  static public ReactorSherlockStub withAcquiredLocks() {
    return new ReactorSherlockStub()
        .withDefaultAcquireResult(false);
  }

  /**
   * Make the stub produce locks with given application instance id
   */
  public ReactorSherlockStub withOwnerId(String ownerId) {
    this.instanceId = InstanceId.of(ownerId);
    return this;
  }

  /**
   * Make the stub produce locks with given lock duration
   */
  public ReactorSherlockStub withLockDuration(Duration duration) {
    this.duration = duration;
    return this;
  }

  /**
   * Make the stub produce return a predefined lock.
   */
  public ReactorSherlockStub withLock(ReactorDistributedLock lock) {
    this.locksById.put(lock.getId(), lock);
    return this;
  }

  private ReactorSherlockStub withDefaultAcquireResult(boolean result) {
    this.defaultLockResult = result;
    return this;
  }

  @Override
  public String getOwnerId() {
    return instanceId.getValue();
  }

  @Override
  public Duration getLockDuration() {
    return duration;
  }

  @Override
  public ReactorDistributedLock createReentrantLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public ReactorDistributedLock createReentrantLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  @Override
  public ReactorDistributedLock createLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public ReactorDistributedLock createLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  @Override
  public ReactorDistributedLock createOverridingLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public ReactorDistributedLock createOverridingLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  private ReactorDistributedLock getLockOrDefault(String lockId) {
    return locksById.getOrDefault(lockId, singleStateLock(lockId, defaultLockResult));
  }
}
