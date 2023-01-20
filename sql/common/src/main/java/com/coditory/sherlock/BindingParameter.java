package com.coditory.sherlock;

import java.util.Objects;

public class BindingParameter {
    private final int index;
    private final String name;

    public BindingParameter(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

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
