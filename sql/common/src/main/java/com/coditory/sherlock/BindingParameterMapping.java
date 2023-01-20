package com.coditory.sherlock;

import java.util.Objects;

public class BindingParameterMapping {
    private final String queryMarker;
    private final Object bindingKey;

    public BindingParameterMapping(String queryMarker, Object bindingKey) {
        this.queryMarker = queryMarker;
        this.bindingKey = bindingKey;
    }

    public String getQueryMarker() {
        return queryMarker;
    }

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
