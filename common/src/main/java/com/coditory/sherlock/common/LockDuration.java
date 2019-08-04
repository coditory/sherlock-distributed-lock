package com.coditory.sherlock.common;

import java.time.Duration;
import java.util.Objects;

import static com.coditory.sherlock.common.util.Preconditions.expectTruncatedToMillis;

public final class LockDuration {
  public static LockDuration of(Duration duration) {
    return new LockDuration(duration);
  }

  public static LockDuration permanent() {
    return new LockDuration(null);
  }

  private final Duration duration;

  private LockDuration(Duration duration) {
    if (duration != null) {
      expectTruncatedToMillis(
        duration, "Expected lock duration truncated to millis. Got: " + duration);
    }
    this.duration = duration;
  }

  public Duration getValue() {
    return duration;
  }

  public boolean isPermanent() {
    return duration == null;
  }

  @Override
  public String toString() {
    return "LockDuration(" + duration + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LockDuration that = (LockDuration) o;
    return Objects.equals(duration, that.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(duration);
  }
}
