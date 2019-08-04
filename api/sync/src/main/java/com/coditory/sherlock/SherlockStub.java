package com.coditory.sherlock;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.OwnerId;

import java.util.HashMap;
import java.util.Map;

/**
 * Use to stub {@link Sherlock} in tests.
 */
public class SherlockStub implements Sherlock {
  private final Map<String, DistributedLock> locksById = new HashMap<>();
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
  public void initialize() {
    // deliberately empty, nothing to initialize
  }

  @Override
  public DistributedLockBuilder createLock() {
    return getLockOrDefault();
  }

  @Override
  public DistributedLockBuilder createReentrantLock() {
    return getLockOrDefault();
  }

  @Override
  public DistributedLockBuilder createOverridingLock() {
    return getLockOrDefault();
  }

  @Override
  public boolean forceReleaseAllLocks() {
    // deliberately empty
    return false;
  }

  private DistributedLockBuilder getLockOrDefault() {
    return new DistributedLockBuilder(this::getLockOrDefault);
  }

  private DistributedLock getLockOrDefault(LockId id, LockDuration duration, OwnerId ownerId) {
    DistributedLockMock defaultLock = DistributedLockMock
      .lockStub(id.getValue(), defaultLockResult);
    return locksById.getOrDefault(id.getValue(), defaultLock);
  }
}
