package com.coditory.sherlock;

import static com.coditory.sherlock.OwnerId.uniqueOwnerId;

interface OwnerIdPolicy {
  static OwnerIdPolicy staticOwnerIdPolicy(String ownerId) {
    return new StaticOwnerIdPolicy(OwnerId.of(ownerId));
  }

  static OwnerIdPolicy uniqueOwnerIdPolicy() {
    return StaticOwnerIdPolicy.RANDOM_OWNER_ID_POLICY;
  }

  static OwnerIdPolicy staticUniqueOwnerIdPolicy() {
    return StaticOwnerIdPolicy.RANDOM_STATIC_OWNER_ID_POLICY;
  }

  OwnerId getOwnerId();
}

class StaticOwnerIdPolicy implements OwnerIdPolicy {
  static final OwnerIdPolicy RANDOM_OWNER_ID_POLICY =
    OwnerId::uniqueOwnerId;
  static final OwnerIdPolicy RANDOM_STATIC_OWNER_ID_POLICY =
    new StaticOwnerIdPolicy(uniqueOwnerId());

  private final OwnerId ownerId;

  StaticOwnerIdPolicy(OwnerId ownerId) {
    this.ownerId = ownerId;
  }

  @Override
  public OwnerId getOwnerId() {
    return ownerId;
  }
}
