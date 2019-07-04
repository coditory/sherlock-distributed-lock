package com.coditory.xlock.common;

import com.coditory.xlock.common.util.XLockPreconditions;

import java.util.Objects;

public class LockId {
  private final String id;

  public LockId(String id) {
    this.id = XLockPreconditions.expectNonEmpty(id, "Expected non empty lockId");
  }

  public String getValue() {
    return id;
  }

  @Override
  public String toString() {
    return "LockId{" +
        "id='" + id + '\'' +
        '}';
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
