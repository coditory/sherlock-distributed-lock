package com.coditory.sherlock;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class InMemoryDistributedLockStorage {
  private static InMemoryDistributedLockStorage INSTANCE = new InMemoryDistributedLockStorage();

  public static InMemoryDistributedLockStorage singleton() {
    return INSTANCE;
  }

  private final Map<LockId, InMemoryDistributedLock> locks = new HashMap<>();

  synchronized public boolean acquire(LockRequest lockRequest, Instant now) {
    dropExpiredLocks(now);
    LockId id = lockRequest.getLockId();
    InMemoryDistributedLock lock = locks.get(id);
    if (lock == null) {
      locks.put(id, InMemoryDistributedLock.fromLockRequest(lockRequest, now));
      return true;
    }
    return false;
  }

  synchronized public boolean acquireOrProlong(LockRequest lockRequest, Instant now) {
    dropExpiredLocks(now);
    LockId id = lockRequest.getLockId();
    InMemoryDistributedLock lock = locks.get(id);
    if (lock == null || lock.isOwnedBy(lockRequest.getOwnerId())) {
      locks.put(id, InMemoryDistributedLock.fromLockRequest(lockRequest, now));
      return true;
    }
    return false;
  }

  synchronized public boolean forceAcquire(LockRequest lockRequest, Instant now) {
    dropExpiredLocks(now);
    LockId id = lockRequest.getLockId();
    locks.put(id, InMemoryDistributedLock.fromLockRequest(lockRequest, now));
    return true;
  }

  synchronized public boolean release(LockId lockId, Instant now, OwnerId ownerId) {
    dropExpiredLocks(now);
    InMemoryDistributedLock lock = locks.get(lockId);
    if (lock != null && lock.isOwnedBy(ownerId)) {
      locks.remove(lockId);
      return true;
    }
    return false;
  }

  synchronized public boolean forceRelease(LockId lockId, Instant now) {
    dropExpiredLocks(now);
    InMemoryDistributedLock lock = locks.get(lockId);
    if (lock != null) {
      locks.remove(lockId);
      return true;
    }
    return false;
  }

  synchronized public boolean forceReleaseAll(Instant now) {
    dropExpiredLocks(now);
    int size = locks.size();
    if (size > 0) {
      locks.clear();
      return true;
    }
    return false;
  }

  private void dropExpiredLocks(Instant now) {
    List<LockId> expired = locks.values().stream()
      .filter(lock -> lock.isExpired(now))
      .map(InMemoryDistributedLock::getId)
      .collect(toList());
    expired.forEach(locks::remove);
  }
}
