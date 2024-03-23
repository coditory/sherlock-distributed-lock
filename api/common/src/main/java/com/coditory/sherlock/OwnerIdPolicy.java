package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;

/**
 * Provides ownerId for locks.
 * It's executed once for every lock, during lock creation.
 * <p>
 * If two JVM processes use the same ownerId, they are treated by Sherlock as the same owner.
 */
public interface OwnerIdPolicy {
    @NotNull
    static OwnerIdPolicy defaultOwnerIdPolicy() {
        return OwnerIdPolicy.uniqueOwnerId();
    }

    @NotNull
    static OwnerIdPolicy staticOwnerId(@NotNull String ownerId) {
        expectNonEmpty(ownerId, "ownerId");
        return new StaticOwnerIdPolicy(ownerId);
    }

    @NotNull
    static OwnerIdPolicy uniqueOwnerId() {
        return StaticOwnerIdPolicy.RANDOM_OWNER_ID_POLICY;
    }

    @NotNull
    static OwnerIdPolicy staticUniqueOwnerId() {
        return StaticOwnerIdPolicy.RANDOM_STATIC_OWNER_ID_POLICY;
    }

    @NotNull
    String getOwnerId();
}

class StaticOwnerIdPolicy implements OwnerIdPolicy {
    static final OwnerIdPolicy RANDOM_OWNER_ID_POLICY = UuidGenerator::uuid;
    static final OwnerIdPolicy RANDOM_STATIC_OWNER_ID_POLICY = new StaticOwnerIdPolicy(UuidGenerator.uuid());

    private final String ownerId;

    StaticOwnerIdPolicy(@NotNull String ownerId) {
        expectNonEmpty(ownerId, "ownerId");
        this.ownerId = ownerId;
    }

    @Override
    @NotNull
    public String getOwnerId() {
        return ownerId;
    }
}
