package com.coditory.xlock.common;

import java.util.Objects;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonEmpty;

public class ServiceInstanceId {
  public static ServiceInstanceId of(String value) {
    return new ServiceInstanceId(value);
  }

  private final String id;

  private ServiceInstanceId(String id) {
    this.id = expectNonEmpty(id, "Expected non empty instance id");
  }

  public String getValue() {
    return id;
  }

  @Override
  public String toString() {
    return "ServiceInstanceId{" +
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
    ServiceInstanceId lockId = (ServiceInstanceId) o;
    return Objects.equals(id, lockId.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
