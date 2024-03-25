package com.coditory.sherlock.sql;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.coditory.sherlock.Preconditions.expect;
import static com.coditory.sherlock.Preconditions.expectNonNull;

public final class BindingParameter {
    private final int index;
    private final String name;

    public BindingParameter(int index, @NotNull String name) {
        expect(index >= 0, "Expected index >= 0. Got: %d", index);
        expectNonNull(name, "name");
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BindingParameter that = (BindingParameter) o;
        return index == that.index && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, name);
    }

    @Override
    public String toString() {
        return "BindingParameter{" +
            "index=" + index +
            ", name='" + name + '\'' +
            '}';
    }
}
