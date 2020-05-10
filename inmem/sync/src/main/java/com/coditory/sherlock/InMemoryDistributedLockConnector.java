package com.coditory.sherlock;

import java.time.Clock;
import java.time.Instant;

class InMemoryDistributedLockConnector implements DistributedLockConnector {
  private final InMemoryDistributedLockStorage storage;
  private final Clock clock;

  InMemoryDistributedLockConnector(Clock clock, InMemoryDistributedLockStorage storage) {
    this.storage = storage;
    this.clock = clock;
  }

  @Override
  public void initialize() {
    // deliberately empty
  }

  @Override
  synchronized public boolean acquire(LockRequest lockRequest) {
    return storage.acquire(lockRequest, now());
  }

  @Override
  synchronized public boolean acquireOrProlong(LockRequest lockRequest) {
    return storage.acquireOrProlong(lockRequest, now());
  }

  @Override
  synchronized public boolean forceAcquire(LockRequest lockRequest) {
    return storage.forceAcquire(lockRequest, now());
  }

  @Override
  synchronized public boolean release(LockId lockId, OwnerId ownerId) {
    return storage.release(lockId, now(), ownerId);
  }

  @Override
  synchronized public boolean forceRelease(LockId lockId) {
    return storage.forceRelease(lockId, now());
  }

  @Override
  public boolean forceReleaseAll() {
    return storage.forceReleaseAll(now());
  }

  @Override
  public LockState getLockState(LockId lockId, OwnerId ownerId) {
    return storage.getLockState(lockId, ownerId, now());
  }

  private Instant now() {
    return clock.instant();
  }
}
