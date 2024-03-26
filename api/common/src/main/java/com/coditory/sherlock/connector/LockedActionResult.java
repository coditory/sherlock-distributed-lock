package com.coditory.sherlock.connector;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class LockedActionResult<T> {
    public static <T> LockedActionResult<T> acquiredResult(T value) {
        return new LockedActionResult<>(true, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> LockedActionResult<T> acquiredEmptyResult() {
        return (LockedActionResult<T>) ACQUIRED_EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <T> LockedActionResult<T> notAcquiredResult() {
        return (LockedActionResult<T>) NOT_ACQUIRED;
    }

    private static final LockedActionResult<Object> ACQUIRED_EMPTY = new LockedActionResult<>(true, null);
    private static final LockedActionResult<Object> NOT_ACQUIRED = new LockedActionResult<>(false, null);

    private final boolean acquired;
    private final T value;

    private LockedActionResult(boolean acquired, T value) {
        this.acquired = acquired;
        this.value = value;
    }

    public boolean acquired() {
        return acquired;
    }

    public T value() {
        return value;
    }

    public LockedActionResult<T> onNotAcquired(Runnable action) {
        if (!acquired) {
            action.run();
        }
        return this;
    }

    public LockedActionResult<T> onAcquired(Consumer<T> action) {
        if (acquired) {
            action.accept(value);
        }
        return this;
    }

    public <R> LockedActionResult<R> mapAcquired(Function<T, R> action) {
        if (acquired) {
            R mapped = action.apply(value);
            return LockedActionResult.acquiredResult(mapped);
        }
        return LockedActionResult.notAcquiredResult();
    }

    public AcquireResult toAcquireResult() {
        return acquired ? AcquireResult.acquiredResult() : AcquireResult.notAcquiredResult();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LockedActionResult<?> that = (LockedActionResult<?>) o;
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
