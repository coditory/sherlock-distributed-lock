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

public class RxJavaDistributedLockBuilder {
  private final LockCreator lockCreator;
  private LockId lockId;
  private LockDuration duration = DEFAULT_LOCK_DURATION;
  private OwnerIdPolicy ownerIdPolicy = DEFAULT_OWNER_ID_POLICY;

  RxJavaDistributedLockBuilder(LockCreator lockCreator) {
    this.lockCreator = lockCreator;
  }

  public RxJavaDistributedLockBuilder withLockId(String lockId) {
    this.lockId = LockId.of(lockId);
    return this;
  }

  RxJavaDistributedLockBuilder withLockDuration(LockDuration duration) {
    this.duration = duration;
    return this;
  }

  public RxJavaDistributedLockBuilder withLockDuration(Duration duration) {
    return withLockDuration(LockDuration.of(duration));
  }

  public RxJavaDistributedLockBuilder withPermanentLockDuration() {
    this.duration = LockDuration.permanent();
    return this;
  }

  public RxJavaDistributedLockBuilder withOwnerId(String ownerId) {
    return withOwnerIdPolicy(staticOwnerIdPolicy(ownerId));
  }

  public RxJavaDistributedLockBuilder withUniqueOwnerId() {
    return withOwnerIdPolicy(uniqueOwnerIdPolicy());
  }

  public RxJavaDistributedLockBuilder withStaticUniqueOwnerId() {
    return withOwnerIdPolicy(staticUniqueOwnerIdPolicy());
  }

  RxJavaDistributedLockBuilder withOwnerIdPolicy(OwnerIdPolicy ownerIdPolicy) {
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

  public RxJavaDistributedLock build() {
    return lockCreator.createLock(lockId, duration, ownerIdPolicy.getOwnerId());
  }

  @FunctionalInterface
  interface LockCreator {
    RxJavaDistributedLock createLock(LockId lockId, LockDuration duration, OwnerId ownerId);
  }
}
