package com.coditory.sherlock.rxjava.test;

import com.coditory.sherlock.rxjava.RxJavaDistributedLock;
import com.coditory.sherlock.rxjava.RxJavaSherlock;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.coditory.sherlock.rxjava.test.RxJavaDistributedLockMock.singleStateLock;

/**
 * Use to stub {@link RxJavaSherlock} in tests.
 */
public final class RxJavaSherlockStub implements RxJavaSherlock {
  private final Map<String, RxJavaDistributedLock> locksById = new HashMap<>();
  private boolean defaultLockResult = true;

  /**
   * Make the stub produce released locks by default
   *
   * @return the instance
   */
  static public RxJavaSherlockStub withReleasedLocks() {
    return new RxJavaSherlockStub()
        .withDefaultAcquireResult(true);
  }

  /**
   * Make the stub produce acquired locks by default
   *
   * @return the instance
   */
  static public RxJavaSherlockStub withAcquiredLocks() {
    return new RxJavaSherlockStub()
        .withDefaultAcquireResult(false);
  }

  /**
   * Make the stub produce return a predefined lock.
   *
   * @param lock returned when creating a lock with the same id
   * @return the instance
   */
  public RxJavaSherlockStub withLock(RxJavaDistributedLock lock) {
    this.locksById.put(lock.getId(), lock);
    return this;
  }

  private RxJavaSherlockStub withDefaultAcquireResult(boolean result) {
    this.defaultLockResult = result;
    return this;
  }

  @Override
  public RxJavaDistributedLock createReentrantLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public RxJavaDistributedLock createReentrantLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  @Override
  public RxJavaDistributedLock createLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public RxJavaDistributedLock createLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  @Override
  public RxJavaDistributedLock createOverridingLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public RxJavaDistributedLock createOverridingLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  private RxJavaDistributedLock getLockOrDefault(String lockId) {
    return locksById.getOrDefault(lockId, singleStateLock(lockId, defaultLockResult));
  }
}
