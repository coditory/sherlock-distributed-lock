package com.coditory.sherlock.connector;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class AcquireResultWithValue<T> {
    public static <T> AcquireResultWithValue<T> acquired(T value) {
        return new AcquireResultWithValue<>(true, value);
    }

    public static <T> AcquireResultWithValue<T> notAcquired() {
        return new AcquireResultWithValue<>(false, null);
    }

    private final boolean acquired;
    private final T value;

    private AcquireResultWithValue(boolean acquired, T value) {
        this.acquired = acquired;
        this.value = value;
    }

    public boolean isAcquired() {
        return acquired;
    }

    public T getValue() {
        return value;
    }

    public AcquireResultWithValue<T> onNotAcquired(Runnable action) {
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

    public <R> AcquireResultWithValue<R> onAcquiredMap(Function<T, R> action) {
        if (acquired) {
            R mapped = action.apply(value);
            return AcquireResultWithValue.acquired(mapped);
        }
        return AcquireResultWithValue.notAcquired();
    }

    public AcquireResult toAcquireResult() {
        return acquired ? AcquireResult.acquired() : AcquireResult.notAcquired();
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
