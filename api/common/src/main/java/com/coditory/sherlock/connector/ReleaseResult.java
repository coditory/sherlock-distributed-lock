package com.coditory.sherlock.connector;

import java.util.Objects;

public final class ReleaseResult {
    public static ReleaseResult of(boolean released) {
        return released ? RELEASED : SKIPPED;
    }

    public static ReleaseResult released() {
        return RELEASED;
    }

    public static ReleaseResult skipped() {
        return SKIPPED;
    }

    private static final ReleaseResult RELEASED = new ReleaseResult(true);
    private static final ReleaseResult SKIPPED = new ReleaseResult(false);

    private final boolean released;

    private ReleaseResult(boolean released) {
        this.released = released;
    }

    public boolean isReleased() {
        return released;
    }

    public ReleaseResult onSkipped(Runnable action) {
        if (!released) {
            action.run();
        }
        return this;
    }

    public ReleaseResult onReleased(Runnable action) {
        if (released) {
            action.run();
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseResult that = (ReleaseResult) o;
        return released == that.released;
    }

    @Override
    public int hashCode() {
        return Objects.hash(released);
    }

    @Override
    public String toString() {
        return "ReleaseResult{" +
            "released=" + released +
            '}';
    }
}
