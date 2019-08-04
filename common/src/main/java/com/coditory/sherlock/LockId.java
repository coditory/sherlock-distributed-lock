package com.coditory.sherlock;

import com.coditory.sherlock.util.Preconditions;

import java.util.Objects;

import static com.coditory.sherlock.util.Preconditions.expectNonEmpty;

final class LockId {
  public static LockId of(String value) {
    return new LockId(value);
  }

  private final String id;

  private LockId(String id) {
    this.id = Preconditions.expectNonEmpty(id, "Expected non empty id");
  }

  public String getValue() {
    return id;
  }

  @Override
  public String toString() {
    return "LockId(" + id + ")";
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
