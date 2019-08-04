package com.coditory.sherlock;

import com.coditory.sherlock.util.UuidGenerator;
import com.coditory.sherlock.util.Preconditions;

import java.util.Objects;

import static com.coditory.sherlock.util.Preconditions.expectNonEmpty;

final class OwnerId {
  public static OwnerId uniqueOwnerId() {
    return new OwnerId(UuidGenerator.uuid());
  }

  public static OwnerId of(String value) {
    return new OwnerId(value);
  }

  private final String id;

  private OwnerId(String id) {
    this.id = Preconditions.expectNonEmpty(id, "Expected non empty instance id");
  }

  public String getValue() {
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
