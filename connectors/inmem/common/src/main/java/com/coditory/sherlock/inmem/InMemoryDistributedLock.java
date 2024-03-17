package com.coditory.sherlock.inmem;

import com.coditory.sherlock.LockDuration;
import com.coditory.sherlock.LockId;
import com.coditory.sherlock.LockRequest;
import com.coditory.sherlock.OwnerId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class InMemoryDistributedLock {
    @NotNull
    static InMemoryDistributedLock fromLockRequest(
            @NotNull LockRequest lockRequest,
            @NotNull Instant acquiredAt
    ) {
        expectNonNull(lockRequest, "lockRequest");
        expectNonNull(acquiredAt, "acquiredAt");
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
            @NotNull LockId id,
            @NotNull OwnerId ownerId,
            @NotNull Instant createdAt,
            @Nullable Instant expiresAt
    ) {
        this.id = expectNonNull(id, "id");
        this.ownerId = expectNonNull(ownerId, "ownerId");
        this.acquiredAt = expectNonNull(createdAt, "createdAt");
        this.expiresAt = expiresAt;
    }

    @NotNull
    LockId getId() {
        return id;
    }

    boolean isActive(@NotNull Instant now) {
        return expiresAt == null
                || expiresAt.isAfter(now);
    }

    boolean isExpired(@NotNull Instant now) {
        return !isActive(now);
    }

    boolean isOwnedBy(@NotNull OwnerId ownerId) {
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
