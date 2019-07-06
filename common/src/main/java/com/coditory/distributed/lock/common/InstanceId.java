package com.coditory.distributed.lock.common;

import java.util.Objects;

import static com.coditory.distributed.lock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.distributed.lock.common.util.UuidGenerator.uuid;

public final class InstanceId {
  public static InstanceId uniqueInstanceId() {
    return new InstanceId(uuid());
  }

  public static InstanceId of(String value) {
    return new InstanceId(value);
  }

  private final String id;

  private InstanceId(String id) {
    this.id = expectNonEmpty(id, "Expected non empty instance id");
  }

  public String getValue() {
    return id;
  }

  @Override
  public String toString() {
    return "InstanceId{id='" + id + "'}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InstanceId instanceId = (InstanceId) o;
    return Objects.equals(id, instanceId.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
