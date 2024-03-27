package com.coditory.sherlock.migrator;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class MigrationResult {
    public static MigrationResult acquiredResult(List<String> executedChangeSets) {
        return new MigrationResult(true, executedChangeSets);
    }

    public static MigrationResult acquiredEmptyResult() {
        return ACQUIRED_EMPTY;
    }

    public static MigrationResult rejectedResult() {
        return REJECTED;
    }

    private static final MigrationResult REJECTED = new MigrationResult(false, List.of());
    private static final MigrationResult ACQUIRED_EMPTY = new MigrationResult(true, List.of());

    private final boolean acquired;
    private final List<String> executedChangeSets;

    private MigrationResult(boolean acquired, List<String> executedChangeSets) {
        this.acquired = acquired;
        this.executedChangeSets = List.copyOf(executedChangeSets);
    }

    public boolean acquired() {
        return acquired;
    }

    public boolean rejected() {
        return !acquired;
    }

    public List<String> executedChangeSets() {
        return executedChangeSets;
    }

    public MigrationResult onAcquired(Consumer<List<String>> action) {
        if (acquired) {
            action.accept(executedChangeSets);
        }
        return this;
    }

    public MigrationResult onRejected(Runnable action) {
        if (!acquired) {
            action.run();
        }
        return this;
    }

    public MigrationResult onFinished(Runnable action) {
        action.run();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MigrationResult that = (MigrationResult) o;
        return acquired == that.acquired && Objects.equals(executedChangeSets, that.executedChangeSets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acquired, executedChangeSets);
    }

    @Override
    public String toString() {
        return "MigrationResult{" +
            "acquired=" + acquired +
            ", executedChangeSets=" + executedChangeSets +
            '}';
    }
}
