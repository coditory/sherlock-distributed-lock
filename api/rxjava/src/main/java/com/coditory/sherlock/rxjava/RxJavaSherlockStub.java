package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.reactive.connector.InitializationResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import io.reactivex.Single;

import java.util.HashMap;
import java.util.Map;

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
  public Single<InitializationResult> initialize() {
    return Single.just(InitializationResult.of(true));
  }

  @Override
  public RxJavaDistributedLockBuilder createLock() {
    return getLockOrDefault();
  }

  @Override
  public RxJavaDistributedLockBuilder createReentrantLock() {
    return getLockOrDefault();
  }

  @Override
  public RxJavaDistributedLockBuilder createOverridingLock() {
    return getLockOrDefault();
  }

  @Override
  public Single<ReleaseResult> forceReleaseAllLocks() {
    return Single.just(ReleaseResult.of(false));
  }

  private RxJavaDistributedLockBuilder getLockOrDefault() {
    return new RxJavaDistributedLockBuilder(this::getLockOrDefault);
  }

  private RxJavaDistributedLock getLockOrDefault(
    LockId id, LockDuration duration, OwnerId ownerId) {
    RxJavaDistributedLockMock defaultLock = RxJavaDistributedLockMock
      .lockStub(id.getValue(), defaultLockResult);
    return locksById.getOrDefault(id.getValue(), defaultLock);
  }
}
