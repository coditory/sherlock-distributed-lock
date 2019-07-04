package com.coditory.xlock.common;

import java.util.Objects;

import static com.coditory.xlock.common.util.UuidGenerator.uuid;
import static com.coditory.xlock.common.util.XLockPreconditions.expectNonEmpty;

public class LockInstanceId {
  public static LockInstanceId uniqueLockInstanceId() {
    return new LockInstanceId(uuid());
  }

  public static LockInstanceId of(String value) {
    return new LockInstanceId(value);
  }

  private final String id;

  LockInstanceId(String id) {
    this.id = expectNonEmpty(id, "Expected non empty lockInstanceId");
  }

  public String getValue() {
    return id;
  }

  @Override
  public String toString() {
    return "LockInstanceId{" +
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
    LockInstanceId lockId = (LockInstanceId) o;
    return Objects.equals(id, lockId.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
