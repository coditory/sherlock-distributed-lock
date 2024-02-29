package com.coditory.sherlock.connector;

import java.util.Objects;

public final class AcquireResult {
    public static final AcquireResult SUCCESS = new AcquireResult(true);
    public static final AcquireResult FAILURE = new AcquireResult(false);

    public static AcquireResult of(boolean value) {
        return value ? SUCCESS : FAILURE;
    }

    private final boolean acquired;

    private AcquireResult(boolean acquired) {
        this.acquired = acquired;
    }

    public boolean isAcquired() {
        return acquired;
    }

    @Override
    public String toString() {
        return "AcquireResult{acquired=" + acquired + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AcquireResult that = (AcquireResult) o;
        return acquired == that.acquired;
    }

    @Override
    public int hashCode() {
        return Objects.hash(acquired);
    }
}
