package com.coditory.sherlock.rxjava;

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

public class RxDistributedLockBuilder {
  private final LockCreator lockCreator;
  private LockId lockId;
  private LockDuration duration = DEFAULT_LOCK_DURATION;
  private OwnerIdPolicy ownerIdPolicy = DEFAULT_OWNER_ID_POLICY;

  RxDistributedLockBuilder(LockCreator lockCreator) {
    this.lockCreator = lockCreator;
  }

  public RxDistributedLockBuilder withLockId(String lockId) {
    this.lockId = LockId.of(lockId);
    return this;
  }

  RxDistributedLockBuilder withLockDuration(LockDuration duration) {
    this.duration = duration;
    return this;
  }

  public RxDistributedLockBuilder withLockDuration(Duration duration) {
    return withLockDuration(LockDuration.of(duration));
  }

  public RxDistributedLockBuilder withPermanentLockDuration() {
    this.duration = LockDuration.permanent();
    return this;
  }

  public RxDistributedLockBuilder withOwnerId(String ownerId) {
    return withOwnerIdPolicy(staticOwnerIdPolicy(ownerId));
  }

  public RxDistributedLockBuilder withUniqueOwnerId() {
    return withOwnerIdPolicy(uniqueOwnerIdPolicy());
  }

  public RxDistributedLockBuilder withStaticUniqueOwnerId() {
    return withOwnerIdPolicy(staticUniqueOwnerIdPolicy());
  }

  RxDistributedLockBuilder withOwnerIdPolicy(OwnerIdPolicy ownerIdPolicy) {
    this.ownerIdPolicy = ownerIdPolicy;
    return this;
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

  public RxDistributedLock build() {
    return lockCreator.createLock(lockId, duration, ownerIdPolicy.getOwnerId());
  }

  @FunctionalInterface
  interface LockCreator {
    RxDistributedLock createLock(LockId lockId, LockDuration duration, OwnerId ownerId);
  }
}
