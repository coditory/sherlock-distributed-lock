package com.coditory.xlock.common;

import java.util.Objects;
import java.util.UUID;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonEmpty;

public class AcquisitionId {
  public static AcquisitionId unqueLockAcquisitionId(LockId lockId) {
    String uuid = UUID.randomUUID().toString();
    return new AcquisitionId(lockId.getValue() + uuid);
  }

  private final String id;

  public AcquisitionId(String id) {
    this.id = expectNonEmpty(id, "Expected non empty acquisitionId");
  }

  public String getValue() {
    return id;
  }

  @Override
  public String toString() {
    return "AcquisitionId{" +
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
    AcquisitionId that = (AcquisitionId) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
