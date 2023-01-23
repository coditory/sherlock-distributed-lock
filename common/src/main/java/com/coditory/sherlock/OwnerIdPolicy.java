package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

import static com.coditory.sherlock.OwnerId.uniqueOwnerId;
import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

public interface OwnerIdPolicy {
    @NotNull
    static OwnerIdPolicy staticOwnerIdPolicy(@NotNull String ownerId) {
        expectNonEmpty(ownerId, "ownerId");
        return new StaticOwnerIdPolicy(OwnerId.of(ownerId));
    }

    @NotNull
    static OwnerIdPolicy uniqueOwnerIdPolicy() {
        return StaticOwnerIdPolicy.RANDOM_OWNER_ID_POLICY;
    }

    @NotNull
    static OwnerIdPolicy staticUniqueOwnerIdPolicy() {
        return StaticOwnerIdPolicy.RANDOM_STATIC_OWNER_ID_POLICY;
    }

    @NotNull
    OwnerId getOwnerId();
}

class StaticOwnerIdPolicy implements OwnerIdPolicy {
    static final OwnerIdPolicy RANDOM_OWNER_ID_POLICY = OwnerId::uniqueOwnerId;
    static final OwnerIdPolicy RANDOM_STATIC_OWNER_ID_POLICY = new StaticOwnerIdPolicy(uniqueOwnerId());

    private final OwnerId ownerId;

    StaticOwnerIdPolicy(@NotNull OwnerId ownerId) {
        expectNonNull(ownerId, "ownerId");
        this.ownerId = ownerId;
    }

    @Override
    @NotNull
    public OwnerId getOwnerId() {
        return ownerId;
    }
}
