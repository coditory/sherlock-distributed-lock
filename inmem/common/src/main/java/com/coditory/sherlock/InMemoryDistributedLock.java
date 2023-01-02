package com.coditory.sherlock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class InMemoryDistributedLock {
    static InMemoryDistributedLock fromLockRequest(
        LockRequest lockRequest, Instant acquiredAt
    ) {
        expectNonNull(lockRequest);
        expectNonNull(acquiredAt);
        Instant releaseAt = Optional.ofNullable(lockRequest.getDuration())
            .map(LockDuration::getValue)
            .map(acquiredAt::plus)
            .map(InMemoryDistributedLock::truncateToMillis)
            .orElse(null);
        return new InMemoryDistributedLock(
            lockRequest.getLockId(),
            lockRequest.getOwnerId(),
            truncateToMillis(acquiredAt),
            releaseAt
        );
    }

    private static Instant truncateToMillis(Instant instant) {
        return instant.truncatedTo(ChronoUnit.MILLIS);
    }

    private final LockId id;
    private final OwnerId ownerId;
    private final Instant acquiredAt;
    private final Instant expiresAt;

    private InMemoryDistributedLock(
        LockId id,
        OwnerId ownerId,
        Instant createdAt,
        Instant expiresAt) {
        this.id = expectNonNull(id);
        this.ownerId = expectNonNull(ownerId);
        this.acquiredAt = expectNonNull(createdAt);
        this.expiresAt = expiresAt;
    }

    LockId getId() {
        return id;
    }

    boolean isActive(Instant now) {
        return expiresAt == null
            || expiresAt.isAfter(now);
    }

    boolean isExpired(Instant now) {
        return !isActive(now);
    }

    boolean isOwnedBy(OwnerId ownerId) {
        return this.ownerId.equals(ownerId);
    }

    @Override
    public String toString() {
        return "InMemoryDistributedLock{" +
            "id=" + id +
            ", ownerId=" + ownerId +
            ", acquiredAt=" + acquiredAt +
            ", expiresAt=" + expiresAt +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InMemoryDistributedLock that = (InMemoryDistributedLock) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(ownerId, that.ownerId) &&
            Objects.equals(acquiredAt, that.acquiredAt) &&
            Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, acquiredAt, expiresAt);
    }
}
