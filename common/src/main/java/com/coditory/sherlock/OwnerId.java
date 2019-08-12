package com.coditory.sherlock;

import java.util.Objects;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;

final class OwnerId {
  static OwnerId uniqueOwnerId() {
    return new OwnerId(UuidGenerator.uuid());
  }

  static OwnerId of(String value) {
    return new OwnerId(value);
  }

  private final String id;

  private OwnerId(String id) {
    this.id = expectNonEmpty(id, "Expected non empty instance id");
  }

  String getValue() {
    return id;
  }

  @Override
  public String toString() {
    return "OwnerId(" + id + ")";
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
