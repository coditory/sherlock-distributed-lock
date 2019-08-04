package com.coditory.sherlock;

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

public class DistributedLockBuilder {
  private final LockCreator lockCreator;
  private LockId lockId;
  private LockDuration duration = DEFAULT_LOCK_DURATION;
  private OwnerIdPolicy ownerIdPolicy = DEFAULT_OWNER_ID_POLICY;

  public DistributedLockBuilder(LockCreator lockCreator) {
    this.lockCreator = lockCreator;
  }

  public DistributedLockBuilder withLockId(String lockId) {
    this.lockId = LockId.of(lockId);
    return this;
  }

  public DistributedLockBuilder withLockDuration(LockDuration duration) {
    this.duration = duration;
    return this;
  }

  public DistributedLockBuilder withLockDuration(Duration duration) {
    return withLockDuration(LockDuration.of(duration));
  }

  public DistributedLockBuilder withPermanentLockDuration() {
    this.duration = LockDuration.permanent();
    return this;
  }

  public DistributedLockBuilder withOwnerId(String ownerId) {
    return withOwnerIdPolicy(staticOwnerIdPolicy(ownerId));
  }

  public DistributedLockBuilder withUniqueOwnerId() {
    return withOwnerIdPolicy(uniqueOwnerIdPolicy());
  }

  public DistributedLockBuilder withStaticUniqueOwnerId() {
    return withOwnerIdPolicy(staticUniqueOwnerIdPolicy());
  }

  public DistributedLockBuilder withOwnerIdPolicy(OwnerIdPolicy ownerIdPolicy) {
    this.ownerIdPolicy = ownerIdPolicy;
    return this;
  }

  public DistributedLock build() {
    return lockCreator.createLock(lockId, duration, ownerIdPolicy.getOwnerId());
  }

  @FunctionalInterface
  public interface LockCreator {
    DistributedLock createLock(LockId lockId, LockDuration duration, OwnerId ownerId);
  }
}
