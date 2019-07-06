package com.coditory.distributed.lock.tests.base

import groovy.transform.CompileStatic

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

import static java.time.Duration.ofDays
import static java.time.Duration.ofMillis
import static java.util.Objects.requireNonNull

@CompileStatic
class UpdatableFixedClock extends Clock {
  public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of('Europe/Warsaw')
  public static final Instant DEFAULT_FIXED_TIME = Instant.parse('2015-12-03T10:15:30.12Z')

  static final UpdatableFixedClock defaultUpdatableFixedClock() {
    return new UpdatableFixedClock(DEFAULT_FIXED_TIME, DEFAULT_ZONE_ID)
  }

  private final ZoneId zoneId
  private Instant fixedTime

  private UpdatableFixedClock(Instant fixedTime, ZoneId zoneId) {
    this.fixedTime = requireNonNull(fixedTime)
    this.zoneId = requireNonNull(zoneId)
  }

  @Override
  ZoneId getZone() {
    return zoneId
  }

  @Override
  UpdatableFixedClock withZone(ZoneId zone) {
    return new UpdatableFixedClock(this.fixedTime, zone)
  }

  @Override
  Instant instant() {
    return fixedTime
  }

  Instant futureInstant(Duration duration = ofDays(1)) {
    return this.fixedTime + duration
  }

  Instant pastInstant(Duration duration = ofDays(1)) {
    return this.fixedTime - duration
  }

  void reset() {
    this.fixedTime = DEFAULT_FIXED_TIME
  }

  void tick(Duration duration = ofMillis(1)) {
    this.fixedTime = this.fixedTime + duration
  }

  void setup(Instant instant) {
    this.fixedTime = instant
  }
}
