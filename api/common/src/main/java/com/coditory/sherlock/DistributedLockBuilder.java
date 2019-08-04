package com.coditory.sherlock;

import java.time.Duration;
import java.util.function.Function;

import static com.coditory.sherlock.OwnerIdPolicy.staticOwnerIdPolicy;
import static com.coditory.sherlock.OwnerIdPolicy.staticUniqueOwnerIdPolicy;
import static com.coditory.sherlock.OwnerIdPolicy.uniqueOwnerIdPolicy;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_OWNER_ID_POLICY;

public final class DistributedLockBuilder<T> {
  private LockId lockId;
  private LockCreator<T> lockCreator;
  private LockDuration duration = DEFAULT_LOCK_DURATION;
  private OwnerIdPolicy ownerIdPolicy = DEFAULT_OWNER_ID_POLICY;

  DistributedLockBuilder(LockCreator<T> lockCreator) {
    this.lockCreator = lockCreator;
  }

  <R> DistributedLockBuilder<R> withMappedLock(Function<T, R> lockMapper) {
    return new DistributedLockBuilder<>((lockId, duration, ownerId) -> {
      T lock = lockCreator.createLock(lockId, duration, ownerId);
      return lockMapper.apply(lock);
    });
  }

  public DistributedLockBuilder<T> withLockId(String lockId) {
    this.lockId = LockId.of(lockId);
    return this;
  }

  DistributedLockBuilder<T> withLockDuration(LockDuration duration) {
    this.duration = duration;
    return this;
  }

  public DistributedLockBuilder<T> withLockDuration(Duration duration) {
    return withLockDuration(LockDuration.of(duration));
  }

  public DistributedLockBuilder<T> withPermanentLockDuration() {
    this.duration = LockDuration.permanent();
    return this;
  }

  public DistributedLockBuilder<T> withOwnerId(String ownerId) {
    return withOwnerIdPolicy(staticOwnerIdPolicy(ownerId));
  }

  public DistributedLockBuilder<T> withUniqueOwnerId() {
    return withOwnerIdPolicy(uniqueOwnerIdPolicy());
  }

  public DistributedLockBuilder<T> withStaticUniqueOwnerId() {
    return withOwnerIdPolicy(staticUniqueOwnerIdPolicy());
  }

  DistributedLockBuilder<T> withOwnerIdPolicy(OwnerIdPolicy ownerIdPolicy) {
    this.ownerIdPolicy = ownerIdPolicy;
    return this;
  }

  public T build() {
    return lockCreator.createLock(lockId, duration, ownerIdPolicy.getOwnerId());
  }

  @FunctionalInterface
  interface LockCreator<T> {
    T createLock(LockId lockId, LockDuration duration, OwnerId ownerId);
  }
}
