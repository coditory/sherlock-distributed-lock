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
 * Use to stub {@link RxSherlock} in tests.
 */
public final class RxSherlockStub implements RxSherlock {
  private final Map<String, RxDistributedLock> locksById = new HashMap<>();
  private boolean defaultLockResult = true;

  /**
   * Make the stub produce released locks by default
   *
   * @return the instance
   */
  static public RxSherlockStub withReleasedLocks() {
    return new RxSherlockStub()
      .withDefaultAcquireResult(true);
  }

  /**
   * Make the stub produce acquired locks by default
   *
   * @return the instance
   */
  static public RxSherlockStub withAcquiredLocks() {
    return new RxSherlockStub()
      .withDefaultAcquireResult(false);
  }

  /**
   * Make the stub produce return a predefined lock.
   *
   * @param lock returned when creating a lock with the same id
   * @return the instance
   */
  public RxSherlockStub withLock(RxDistributedLock lock) {
    this.locksById.put(lock.getId(), lock);
    return this;
  }

  private RxSherlockStub withDefaultAcquireResult(boolean result) {
    this.defaultLockResult = result;
    return this;
  }

  @Override
  public Single<InitializationResult> initialize() {
    return Single.just(InitializationResult.of(true));
  }

  @Override
  public RxDistributedLockBuilder createLock() {
    return getLockOrDefault();
  }

  @Override
  public RxDistributedLockBuilder createReentrantLock() {
    return getLockOrDefault();
  }

  @Override
  public RxDistributedLockBuilder createOverridingLock() {
    return getLockOrDefault();
  }

  @Override
  public Single<ReleaseResult> forceReleaseAllLocks() {
    return Single.just(ReleaseResult.of(false));
  }

  private RxDistributedLockBuilder getLockOrDefault() {
    return new RxDistributedLockBuilder(this::getLockOrDefault);
  }

  private RxDistributedLock getLockOrDefault(
    LockId id, LockDuration duration, OwnerId ownerId) {
    RxDistributedLockMock defaultLock = RxDistributedLockMock
      .lockStub(id.getValue(), defaultLockResult);
    return locksById.getOrDefault(id.getValue(), defaultLock);
  }
}
