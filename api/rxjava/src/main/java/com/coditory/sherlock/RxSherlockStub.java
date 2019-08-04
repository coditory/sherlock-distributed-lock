package com.coditory.sherlock;

import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
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
  public DistributedLockBuilder<RxDistributedLock> createLock() {
    return getLockOrDefault();
  }

  @Override
  public DistributedLockBuilder<RxDistributedLock> createReentrantLock() {
    return getLockOrDefault();
  }

  @Override
  public DistributedLockBuilder<RxDistributedLock> createOverridingLock() {
    return getLockOrDefault();
  }

  @Override
  public Single<ReleaseResult> forceReleaseAllLocks() {
    return Single.just(ReleaseResult.of(false));
  }

  private DistributedLockBuilder<RxDistributedLock> getLockOrDefault() {
    return new DistributedLockBuilder<>(this::getLockOrDefault);
  }

  private RxDistributedLock getLockOrDefault(
    LockId id, LockDuration duration, OwnerId ownerId) {
    RxDistributedLockMock defaultLock = RxDistributedLockMock
      .lockStub(id.getValue(), defaultLockResult);
    return locksById.getOrDefault(id.getValue(), defaultLock);
  }
}
