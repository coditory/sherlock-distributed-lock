package com.coditory.xlock.common;

import com.coditory.xlock.common.util.XLockPreconditions;

import java.util.Objects;

public class InstanceId {
  private final String id;

  public InstanceId(String id) {
    this.id = XLockPreconditions.expectNonEmpty(id, "Expected non empty instance id");
  }

  public String getValue() {
    return id;
  }

  @Override
  public String toString() {
    return "InstanceId{" +
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
    InstanceId lockId = (InstanceId) o;
    return Objects.equals(id, lockId.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
