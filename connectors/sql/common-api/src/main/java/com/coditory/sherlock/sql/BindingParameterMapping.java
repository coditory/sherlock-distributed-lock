package com.coditory.sherlock.sql;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

public final class BindingParameterMapping {
    private final String queryMarker;
    private final Object bindingKey;

    public BindingParameterMapping(@NotNull String queryMarker, @NotNull Object bindingKey) {
        expectNonEmpty(queryMarker, "queryMarker");
        expectNonNull(bindingKey, "bindingKey");
        this.queryMarker = queryMarker;
        this.bindingKey = bindingKey;
    }

    @NotNull
    public String getQueryMarker() {
        return queryMarker;
    }

    @NotNull
    public Object getBindingKey() {
        return bindingKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BindingParameterMapping that = (BindingParameterMapping) o;
        return Objects.equals(queryMarker, that.queryMarker) && Objects.equals(bindingKey, that.bindingKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryMarker, bindingKey);
    }

    @Override
    public String toString() {
        return "BindingParameter{" +
                "queryMarker='" + queryMarker + '\'' +
                ", bindingKey=" + bindingKey +
                '}';
    }
}
