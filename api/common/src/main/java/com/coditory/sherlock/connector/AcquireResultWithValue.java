package com.coditory.sherlock.connector;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class AcquireResultWithValue<T> {
    public static <T> AcquireResultWithValue<T> acquiredResult(T value) {
        return new AcquireResultWithValue<>(true, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> AcquireResultWithValue<T> acquiredEmptyResult() {
        return (AcquireResultWithValue<T>) ACQUIRED_EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <T> AcquireResultWithValue<T> rejectedResult() {
        return (AcquireResultWithValue<T>) REJECTED;
    }

    private static final AcquireResultWithValue<Object> ACQUIRED_EMPTY = new AcquireResultWithValue<>(true, null);
    private static final AcquireResultWithValue<Object> REJECTED = new AcquireResultWithValue<>(false, null);

    private final boolean acquired;
    private final T value;

    private AcquireResultWithValue(boolean acquired, T value) {
        this.acquired = acquired;
        this.value = value;
    }

    public boolean acquired() {
        return acquired;
    }

    public boolean rejected() {
        return !acquired;
    }

    public T value() {
        return value;
    }

    public AcquireResultWithValue<T> onRejected(Runnable action) {
        if (!acquired) {
            action.run();
        }
        return this;
    }

    public AcquireResultWithValue<T> onAcquired(Consumer<T> action) {
        if (acquired) {
            action.accept(value);
        }
        return this;
    }

    public <R> AcquireResultWithValue<R> mapAcquired(Function<T, R> action) {
        if (acquired) {
            R mapped = action.apply(value);
            return AcquireResultWithValue.acquiredResult(mapped);
        }
        return AcquireResultWithValue.rejectedResult();
    }

    public AcquireResultWithValue<T> onFinished(Runnable action) {
        action.run();
        return this;
    }

    public AcquireResult toAcquireResult() {
        return acquired ? AcquireResult.acquiredResult() : AcquireResult.rejectedResult();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcquireResultWithValue<?> that = (AcquireResultWithValue<?>) o;
        return acquired == that.acquired && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acquired, value);
    }

    @Override
    public String toString() {
        return "AcquireResultWithValue{" +
            "acquired=" + acquired +
            ", value=" + value +
            '}';
    }
}
