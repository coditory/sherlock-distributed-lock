package com.coditory.sherlock.connector;

import java.util.Objects;

public final class ReleaseResult {
    public static final ReleaseResult SUCCESS = new ReleaseResult(true);
    public static final ReleaseResult FAILURE = new ReleaseResult(false);

    public static ReleaseResult of(boolean value) {
        return value ? SUCCESS : FAILURE;
    }

    private final boolean released;

    private ReleaseResult(boolean released) {
        this.released = released;
    }

    public boolean isReleased() {
        return released;
    }

    @Override
    public String toString() {
        return "ReleaseResult{released=" + released + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReleaseResult that = (ReleaseResult) o;
        return released == that.released;
    }

    @Override
    public int hashCode() {
        return Objects.hash(released);
    }
}
