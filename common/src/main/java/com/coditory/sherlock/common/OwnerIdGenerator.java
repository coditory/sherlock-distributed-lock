package com.coditory.sherlock.common;

import static com.coditory.sherlock.common.OwnerId.uniqueOwnerId;

public interface OwnerIdGenerator {
  OwnerIdGenerator RANDOM_OWNER_ID_GENERATOR = OwnerId::uniqueOwnerId;
  OwnerIdGenerator RANDOM_STATIC_OWNER_ID_GENERATOR = new StaticOwnerIdGenerator(uniqueOwnerId());

  static OwnerIdGenerator staticOwnerIdGenerator(String ownerId) {
    return new StaticOwnerIdGenerator(OwnerId.of(ownerId));
  }

  OwnerId getOwnerId();
}

class StaticOwnerIdGenerator implements OwnerIdGenerator {
  private final OwnerId ownerId;

  public StaticOwnerIdGenerator(OwnerId ownerId) {
    this.ownerId = ownerId;
  }

  @Override
  public OwnerId getOwnerId() {
    return ownerId;
  }
}
