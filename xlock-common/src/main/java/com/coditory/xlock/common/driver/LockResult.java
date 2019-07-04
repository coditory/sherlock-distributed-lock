package com.coditory.xlock.common.driver;

import com.coditory.xlock.common.AcquisitionId;
import com.coditory.xlock.common.LockId;

import java.util.Objects;
import java.util.Optional;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

public class LockResult {
  public static LockResult lockGranted(LockId lockId, AcquisitionId acquisitionId) {
    return new LockResult(
        expectNonNull(lockId),
        expectNonNull(acquisitionId));
  }

  public static LockResult lockRefused(LockId lockId) {
    return new LockResult(expectNonNull(lockId), null);
  }

  private final LockId lockId;
  private final AcquisitionId acquisitionId;

  private LockResult(LockId lockId, AcquisitionId acquisitionId) {
    this.lockId = expectNonNull(lockId);
    this.acquisitionId = acquisitionId;
  }

  public LockId getLockId() {
    return lockId;
  }

  public Optional<AcquisitionId> getAcquisitionId() {
    return Optional.ofNullable(acquisitionId);
  }

  public boolean isLocked() {
    return acquisitionId != null;
  }

  @Override
  public String toString() {
    return "XlockSuccessfulLockResult{" +
        "lockId=" + lockId +
        ", acquisitionId=" + acquisitionId +
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
    LockResult that = (LockResult) o;
    return Objects.equals(lockId, that.lockId) &&
        Objects.equals(acquisitionId, that.acquisitionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockId, acquisitionId);
  }

}
