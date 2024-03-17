package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

import static com.coditory.sherlock.Preconditions.expectTruncatedToMillis;

public final class LockDuration {
    @NotNull
    public static LockDuration of(@Nullable Duration duration) {
        return new LockDuration(duration);
    }

    @NotNull
    public static LockDuration permanent() {
        return new LockDuration(null);
    }

    private final Duration duration;

    private LockDuration(Duration duration) {
        if (duration != null) {
            expectTruncatedToMillis(duration, "duration");
        }
        this.duration = duration;
    }

    @Nullable
    public Duration getValue() {
        return duration;
    }

    public boolean isPermanent() {
        return duration == null;
    }

    @Override
    public String toString() {
        return "LockDuration(" + duration + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LockDuration that = (LockDuration) o;
        return Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration);
    }
}
