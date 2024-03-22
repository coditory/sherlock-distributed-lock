package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;

public final class LockId {
    public static LockId of(String value) {
        return new LockId(value);
    }

    private final String id;

    private LockId(@NotNull String lockId) {
        this.id = expectNonEmpty(lockId, "lockId");
    }

    public String getValue() {
        return id;
    }

    @Override
    public String toString() {
        return Objects.toString(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LockId lockId = (LockId) o;
        return Objects.equals(id, lockId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
