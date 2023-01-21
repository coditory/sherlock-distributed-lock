package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class LockRequest {
    private final LockId lockId;
    private final OwnerId ownerId;
    private final LockDuration duration;

    public LockRequest(
            @NotNull LockId lockId,
            @NotNull OwnerId ownerId,
            @Nullable LockDuration duration
    ) {
        this.lockId = expectNonNull(lockId, "lockId");
        this.ownerId = expectNonNull(ownerId, "ownerId");
        this.duration = duration;
    }

    public LockRequest(
            @NotNull LockId lockId,
            @NotNull OwnerId ownerId
    ) {
        this(lockId, ownerId, null);
    }

    @NotNull
    public LockId getLockId() {
        return lockId;
    }

    @NotNull
    public OwnerId getOwnerId() {
        return ownerId;
    }

    @Nullable
    public LockDuration getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LockRequest request = (LockRequest) o;
        return Objects.equals(lockId, request.lockId) &&
                Objects.equals(ownerId, request.ownerId) &&
                Objects.equals(duration, request.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lockId, ownerId, duration);
    }

    @Override
    public String toString() {
        return "LockRequest{" +
                "lockId=" + lockId +
                ", ownerId=" + ownerId +
                ", duration=" + duration +
                '}';
    }
}
