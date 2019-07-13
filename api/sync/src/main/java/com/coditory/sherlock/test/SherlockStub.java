package com.coditory.sherlock.test;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.common.InstanceId;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.test.DistributedLockMock.singleStateLock;

/**
 * Use to stub {@link Sherlock} in tests.
 */
public class SherlockStub implements Sherlock {
  private final Map<String, DistributedLock> locksById = new HashMap<>();
  private InstanceId instanceId = InstanceId.of("tested-instance");
  private Duration duration = DEFAULT_LOCK_DURATION;
  private boolean defaultLockResult = true;

  /**
   * Make the stub produce released locks by default
   */
  public static SherlockStub withReleasedLocks() {
    return new SherlockStub()
        .withDefaultAcquireResult(true);
  }

  /**
   * Make the stub produce acquired locks by default
   */
  public static SherlockStub withAcquiredLocks() {
    return new SherlockStub()
        .withDefaultAcquireResult(false);
  }

  /**
   * Make the stub produce locks with given application instance id
   */
  public SherlockStub withOwnerId(String ownerId) {
    this.instanceId = InstanceId.of(ownerId);
    return this;
  }

  /**
   * Make the stub produce locks with given lock duration
   */
  public SherlockStub withLockDuration(Duration duration) {
    this.duration = duration;
    return this;
  }

  /**
   * Make the stub produce return a predefined lock.
   */
  public SherlockStub withLock(DistributedLock lock) {
    this.locksById.put(lock.getId(), lock);
    return this;
  }

  private SherlockStub withDefaultAcquireResult(boolean result) {
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
  public DistributedLock createReentrantLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public DistributedLock createReentrantLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  @Override
  public DistributedLock createLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public DistributedLock createLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  @Override
  public DistributedLock createOverridingLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public DistributedLock createOverridingLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  private DistributedLock getLockOrDefault(String lockId) {
    return locksById.getOrDefault(lockId, singleStateLock(lockId, defaultLockResult));
  }
}
