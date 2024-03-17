package com.coditory.sherlock.inmem;

import com.coditory.sherlock.LockId;
import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.OwnerId;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static java.util.stream.Collectors.toList;

public class InMemoryDistributedLockStorage {
    private static final InMemoryDistributedLockStorage INSTANCE = new InMemoryDistributedLockStorage();

    @NotNull
    public static InMemoryDistributedLockStorage singleton() {
        return INSTANCE;
    }

    private final Map<LockId, InMemoryDistributedLock> locks = new HashMap<>();

    synchronized public boolean acquire(@NotNull LockRequest lockRequest, @NotNull Instant now) {
        expectNonNull(lockRequest, "lockRequest");
        expectNonNull(now, "now");
        dropExpiredLocks(now);
        LockId id = lockRequest.getLockId();
        InMemoryDistributedLock lock = locks.get(id);
        if (lock == null) {
            locks.put(id, InMemoryDistributedLock.fromLockRequest(lockRequest, now));
            return true;
        }
        return false;
    }

    synchronized public boolean acquireOrProlong(@NotNull LockRequest lockRequest, @NotNull Instant now) {
        expectNonNull(lockRequest, "lockRequest");
        expectNonNull(now, "now");
        dropExpiredLocks(now);
        LockId id = lockRequest.getLockId();
        InMemoryDistributedLock lock = locks.get(id);
        if (lock == null || lock.isOwnedBy(lockRequest.getOwnerId())) {
            locks.put(id, InMemoryDistributedLock.fromLockRequest(lockRequest, now));
            return true;
        }
        return false;
    }

    synchronized public boolean forceAcquire(@NotNull LockRequest lockRequest, @NotNull Instant now) {
        expectNonNull(lockRequest, "lockRequest");
        expectNonNull(now, "now");
        dropExpiredLocks(now);
        LockId id = lockRequest.getLockId();
        locks.put(id, InMemoryDistributedLock.fromLockRequest(lockRequest, now));
        return true;
    }

    synchronized public boolean release(@NotNull LockId lockId, @NotNull Instant now, @NotNull OwnerId ownerId) {
        expectNonNull(lockId, "lockId");
        expectNonNull(now, "now");
        expectNonNull(ownerId, "ownerId");
        dropExpiredLocks(now);
        InMemoryDistributedLock lock = locks.get(lockId);
        if (lock != null && lock.isOwnedBy(ownerId)) {
            locks.remove(lockId);
            return true;
        }
        return false;
    }

    synchronized public boolean forceRelease(@NotNull LockId lockId, @NotNull Instant now) {
        expectNonNull(lockId, "lockId");
        expectNonNull(now, "now");
        dropExpiredLocks(now);
        InMemoryDistributedLock lock = locks.get(lockId);
        if (lock != null) {
            locks.remove(lockId);
            return true;
        }
        return false;
    }

    synchronized public boolean forceReleaseAll(@NotNull Instant now) {
        expectNonNull(now, "now");
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
