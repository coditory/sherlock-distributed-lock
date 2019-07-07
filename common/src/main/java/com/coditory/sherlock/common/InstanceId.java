package com.coditory.sherlock.common;

import com.coditory.sherlock.common.util.Preconditions;
import com.coditory.sherlock.common.util.UuidGenerator;

import java.util.Objects;

import static com.coditory.sherlock.common.util.Preconditions.expectNonEmpty;

public final class InstanceId {
  public static InstanceId uniqueInstanceId() {
    return new InstanceId(UuidGenerator.uuid());
  }

  public static InstanceId of(String value) {
    return new InstanceId(value);
  }

  private final String id;

  private InstanceId(String id) {
    this.id = Preconditions.expectNonEmpty(id, "Expected non empty instance id");
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
