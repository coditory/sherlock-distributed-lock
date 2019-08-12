package com.coditory.sherlock;

import java.time.Duration;
import java.util.Objects;

import static com.coditory.sherlock.Preconditions.expectTruncatedToMillis;

final class LockDuration {
  static LockDuration of(Duration duration) {
    return new LockDuration(duration);
  }

  static LockDuration permanent() {
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

  Duration getValue() {
    return duration;
  }

  boolean isPermanent() {
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
