package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectTruncatedToMillis;

public record LockRequest(
    @NotNull String lockId,
    @NotNull String ownerId,
    @Nullable Duration duration
) {
    public LockRequest {
        expectNonEmpty(lockId, "lockId");
        expectNonEmpty(ownerId, "ownerId");
        if (duration != null) {
            expectTruncatedToMillis(duration, "duration");
        }
    }

    public LockRequest(
        @NotNull String lockId,
        @NotNull String ownerId
    ) {
        this(lockId, ownerId, null);
    }
}
