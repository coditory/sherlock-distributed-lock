package com.coditory.sherlock.connector;

import java.util.Objects;

public final class InitializationResult {
    public static InitializationResult of(boolean initialized) {
        return initialized ? INITIALIZED : SKIPPED;
    }

    public static InitializationResult initializedResult() {
        return INITIALIZED;
    }

    public static InitializationResult skippedResult() {
        return SKIPPED;
    }

    private static final InitializationResult INITIALIZED = new InitializationResult(true);
    private static final InitializationResult SKIPPED = new InitializationResult(false);

    private final boolean initialized;

    private InitializationResult(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean initialized() {
        return initialized;
    }

    public InitializationResult onSkipped(Runnable action) {
        if (!initialized) {
            action.run();
        }
        return this;
    }

    public InitializationResult onInitialized(Runnable action) {
        if (initialized) {
            action.run();
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InitializationResult that = (InitializationResult) o;
        return initialized == that.initialized;
    }

    @Override
    public int hashCode() {
        return Objects.hash(initialized);
    }

    @Override
    public String toString() {
        return "InitializationResult{" +
            "initialized=" + initialized +
            '}';
    }
}
