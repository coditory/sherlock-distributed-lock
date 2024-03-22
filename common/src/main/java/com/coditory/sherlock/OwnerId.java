package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;

public final class OwnerId {
    @NotNull
    public static OwnerId uniqueOwnerId() {
        return new OwnerId(UuidGenerator.uuid());
    }

    @NotNull
    public static OwnerId of(@NotNull String value) {
        return new OwnerId(value);
    }

    private final String id;

    private OwnerId(@NotNull String ownerId) {
        this.id = expectNonEmpty(ownerId, "ownerId");
    }

    @NotNull
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
        OwnerId ownerId = (OwnerId) o;
        return Objects.equals(id, ownerId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
