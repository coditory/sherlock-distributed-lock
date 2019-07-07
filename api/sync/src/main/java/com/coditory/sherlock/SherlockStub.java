package com.coditory.sherlock;

import com.coditory.sherlock.common.InstanceId;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.coditory.sherlock.DistributedLockMock.singleStateLock;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_LOCK_DURATION;

public class SherlockStub implements Sherlock {
  private final Map<String, DistributedLock> locksById = new HashMap<>();
  private InstanceId instanceId = InstanceId.of("tested-instance");
  private Duration duration = DEFAULT_LOCK_DURATION;
  private boolean defaultLockResult = true;

  public static SherlockStub withOpenedLocks() {
    return new SherlockStub()
        .withDefaultAcquireResult(true);
  }

  public static SherlockStub withClosedLocks() {
    return new SherlockStub()
        .withDefaultAcquireResult(false);
  }

  public SherlockStub withServiceInstanceId(String instanceId) {
    this.instanceId = InstanceId.of(instanceId);
    return this;
  }

  public SherlockStub withLockDuration(Duration duration) {
    this.duration = duration;
    return this;
  }

  public SherlockStub withLock(DistributedLock lock) {
    this.locksById.put(lock.getId(), lock);
    return this;
  }

  private SherlockStub withDefaultAcquireResult(boolean result) {
    this.defaultLockResult = result;
    return this;
  }

  @Override
  public String getInstanceId() {
    return instanceId.getValue();
  }

  @Override
  public Duration getLockDuration() {
    return duration;
  }

  @Override
  public DistributedLock createReentrantLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public DistributedLock createReentrantLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  @Override
  public DistributedLock createLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public DistributedLock createLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  @Override
  public DistributedLock createOverridingLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public DistributedLock createOverridingLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  private DistributedLock getLockOrDefault(String lockId) {
    return locksById.getOrDefault(lockId, singleStateLock(lockId, defaultLockResult));
  }
}
