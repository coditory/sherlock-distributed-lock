package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.LockId;
import com.coditory.sherlock.common.OwnerId;
import com.coditory.sherlock.common.OwnerIdPolicy;

import java.time.Duration;

import static com.coditory.sherlock.common.OwnerIdPolicy.staticOwnerIdPolicy;
import static com.coditory.sherlock.common.OwnerIdPolicy.staticUniqueOwnerIdPolicy;
import static com.coditory.sherlock.common.OwnerIdPolicy.uniqueOwnerIdPolicy;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_OWNER_ID_POLICY;

public class ReactiveDistributedLockBuilder<T extends ReactiveDistributedLockBuilder> {
  private final LockCreator lockCreator;
  private LockId lockId;
  private LockDuration duration = DEFAULT_LOCK_DURATION;
  private OwnerIdPolicy ownerIdPolicy = DEFAULT_OWNER_ID_POLICY;

  ReactiveDistributedLockBuilder(LockCreator lockCreator) {
    this.lockCreator = lockCreator;
  }

  public LockId getLockId() {
    return lockId;
  }

  public LockDuration getDuration() {
    return duration;
  }

  public OwnerIdPolicy getOwnerIdPolicy() {
    return ownerIdPolicy;
  }

  public T withLockId(String lockId) {
    this.lockId = LockId.of(lockId);
    return instance();
  }

  T withLockDuration(LockDuration duration) {
    this.duration = duration;
    return instance();
  }

  public T withLockDuration(Duration duration) {
    return withLockDuration(LockDuration.of(duration));
  }

  public T withPermanentLockDuration() {
    this.duration = LockDuration.permanent();
    return instance();
  }

  public T withOwnerId(String ownerId) {
    return withOwnerIdPolicy(staticOwnerIdPolicy(ownerId));
  }

  public T withUniqueOwnerId() {
    return withOwnerIdPolicy(uniqueOwnerIdPolicy());
  }

  public T withStaticUniqueOwnerId() {
    return withOwnerIdPolicy(staticUniqueOwnerIdPolicy());
  }

  T withOwnerIdPolicy(OwnerIdPolicy ownerIdPolicy) {
    this.ownerIdPolicy = ownerIdPolicy;
    return instance();
  }

  @SuppressWarnings("unchecked")
  private T instance() {
    // builder inheritance
    return (T) this;
  }

  public ReactiveDistributedLock build() {
    return lockCreator.createLock(lockId, duration, ownerIdPolicy.getOwnerId());
  }

  @FunctionalInterface
  interface LockCreator {
    ReactiveDistributedLock createLock(LockId lockId, LockDuration duration, OwnerId ownerId);
  }
}
