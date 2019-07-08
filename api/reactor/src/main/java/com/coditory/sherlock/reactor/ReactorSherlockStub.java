package com.coditory.sherlock.reactor;

import com.coditory.sherlock.common.InstanceId;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.reactor.ReactorDistributedLockMock.singleStateLock;

public final class ReactorSherlockStub implements ReactorSherlock {
  private final Map<String, ReactorDistributedLock> locksById = new HashMap<>();
  private InstanceId instanceId = InstanceId.of("tested-instance");
  private Duration duration = DEFAULT_LOCK_DURATION;
  private boolean defaultLockResult = true;

  static public ReactorSherlockStub withOpenedLocks() {
    return new ReactorSherlockStub()
        .withDefaultAcquireResult(true);
  }

  static public ReactorSherlockStub withClosedLocks() {
    return new ReactorSherlockStub()
        .withDefaultAcquireResult(false);
  }

  public ReactorSherlockStub withServiceInstanceId(String instanceId) {
    this.instanceId = InstanceId.of(instanceId);
    return this;
  }

  public ReactorSherlockStub withLockDuration(Duration duration) {
    this.duration = duration;
    return this;
  }

  public ReactorSherlockStub withLock(ReactorDistributedLock lock) {
    this.locksById.put(lock.getId(), lock);
    return this;
  }

  private ReactorSherlockStub withDefaultAcquireResult(boolean result) {
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
  public ReactorDistributedLock createReentrantLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public ReactorDistributedLock createReentrantLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  @Override
  public ReactorDistributedLock createLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public ReactorDistributedLock createLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  @Override
  public ReactorDistributedLock createOverridingLock(String lockId) {
    return getLockOrDefault(lockId);
  }

  @Override
  public ReactorDistributedLock createOverridingLock(String lockId, Duration duration) {
    return getLockOrDefault(lockId);
  }

  private ReactorDistributedLock getLockOrDefault(String lockId) {
    return locksById.getOrDefault(lockId, singleStateLock(lockId, defaultLockResult));
  }
}
