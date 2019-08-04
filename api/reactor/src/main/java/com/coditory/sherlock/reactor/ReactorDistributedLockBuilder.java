package com.coditory.sherlock.reactor;

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

public class ReactorDistributedLockBuilder {
  private final LockCreator lockCreator;
  private LockId lockId;
  private LockDuration duration = DEFAULT_LOCK_DURATION;
  private OwnerIdPolicy ownerIdPolicy = DEFAULT_OWNER_ID_POLICY;

  ReactorDistributedLockBuilder(LockCreator lockCreator) {
    this.lockCreator = lockCreator;
  }

  public ReactorDistributedLockBuilder withLockId(String lockId) {
    this.lockId = LockId.of(lockId);
    return this;
  }

  ReactorDistributedLockBuilder withLockDuration(LockDuration duration) {
    this.duration = duration;
    return this;
  }

  public ReactorDistributedLockBuilder withLockDuration(Duration duration) {
    return withLockDuration(LockDuration.of(duration));
  }

  public ReactorDistributedLockBuilder withPermanentLockDuration() {
    this.duration = LockDuration.permanent();
    return this;
  }

  public ReactorDistributedLockBuilder withOwnerId(String ownerId) {
    return withOwnerIdPolicy(staticOwnerIdPolicy(ownerId));
  }

  public ReactorDistributedLockBuilder withUniqueOwnerId() {
    return withOwnerIdPolicy(uniqueOwnerIdPolicy());
  }

  public ReactorDistributedLockBuilder withStaticUniqueOwnerId() {
    return withOwnerIdPolicy(staticUniqueOwnerIdPolicy());
  }

  ReactorDistributedLockBuilder withOwnerIdPolicy(OwnerIdPolicy ownerIdPolicy) {
    this.ownerIdPolicy = ownerIdPolicy;
    return this;
  }

  public ReactorDistributedLock build() {
    return lockCreator.createLock(lockId, duration, ownerIdPolicy.getOwnerId());
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

  @FunctionalInterface
  interface LockCreator {
    ReactorDistributedLock createLock(LockId lockId, LockDuration duration, OwnerId ownerId);
  }
}
