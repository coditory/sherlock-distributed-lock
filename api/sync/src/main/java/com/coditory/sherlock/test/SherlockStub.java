package com.coditory.sherlock.test;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.OwnerId;

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
  private OwnerId ownerId = OwnerId.of("tested-instance");
  private LockDuration duration = DEFAULT_LOCK_DURATION;
  private boolean defaultLockResult = true;

  /**
   * Make the stub produce released locks by default
   *
   * @return the stub instance
   */
  public static SherlockStub withReleasedLocks() {
    return new SherlockStub()
        .withDefaultAcquireResult(true);
  }

  /**
   * Make the stub produce acquired locks by default
   *
   * @return the stub instance
   */
  public static SherlockStub withAcquiredLocks() {
    return new SherlockStub()
        .withDefaultAcquireResult(false);
  }

  /**
   * Make the stub produce locks with given application instance id
   *
   * @param ownerId lock owner id
   * @return the stub instance
   */
  public SherlockStub withOwnerId(String ownerId) {
    this.ownerId = OwnerId.of(ownerId);
    return this;
  }

  /**
   * Make the stub produce locks with given lock duration
   *
   * @param duration lock duration
   * @return the stub instance
   */
  public SherlockStub withLockDuration(Duration duration) {
    this.duration = LockDuration.of(duration);
    return this;
  }

  /**
   * Make the stub produce return a predefined lock.
   *
   * @param lock returned when creating a lock with the same id
   * @return the stub instance
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
    return ownerId.getValue();
  }

  @Override
  public Duration getLockDuration() {
    return duration.getValue();
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
