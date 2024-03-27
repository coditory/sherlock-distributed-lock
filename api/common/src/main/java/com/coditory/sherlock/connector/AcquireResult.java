package com.coditory.sherlock.connector;

import java.util.Objects;

public final class AcquireResult {
    public static AcquireResult of(boolean acquired) {
        return acquired ? ACQUIRED : REJECTED;
    }

    public static AcquireResult acquiredResult() {
        return ACQUIRED;
    }

    public static AcquireResult rejectedResult() {
        return REJECTED;
    }

    private static final AcquireResult ACQUIRED = new AcquireResult(true);
    private static final AcquireResult REJECTED = new AcquireResult(false);

    private final boolean acquired;

    private AcquireResult(boolean acquired) {
        this.acquired = acquired;
    }

    public boolean acquired() {
        return acquired;
    }

    public boolean rejected() {
        return !acquired;
    }

    public AcquireResult onRejected(Runnable action) {
        if (!acquired) {
            action.run();
        }
        return this;
    }

    public AcquireResult onAcquired(Runnable action) {
        if (acquired) {
            action.run();
        }
        return this;
    }

    public AcquireResult onFinished(Runnable action) {
        action.run();
        return this;
    }

    public <T> AcquireResultWithValue<T> toAcquiredWithValue(T value) {
        if (acquired) {
            return AcquireResultWithValue.acquiredResult(value);
        }
        return AcquireResultWithValue.rejectedResult();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcquireResult that = (AcquireResult) o;
        return acquired == that.acquired;
    }

    @Override
    public int hashCode() {
        return Objects.hash(acquired);
    }

    @Override
    public String toString() {
        return "AcquireResult{" +
            "acquired=" + acquired +
            '}';
    }
}
