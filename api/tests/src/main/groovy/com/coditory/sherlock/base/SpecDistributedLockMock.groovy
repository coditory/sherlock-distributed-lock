package com.coditory.sherlock.base

import java.time.Duration
import java.time.Instant

interface SpecDistributedLockMock {
  String getId();

  boolean acquire();

  boolean acquire(Duration duration);

  boolean acquireForever();

  boolean release();

  void markAsLocked(Instant expiresAt);

  void markAsLocked(Duration duration);

  void markAsLocked();

  void markAsAcquired(Instant expiresAt);

  void markAsAcquired(Duration duration);

  void markAsAcquired();

  void markAsUnlocked();

  int successfulReleases();

  int successfulAcquisitions();

  int releases();

  int acquisitions();

  int rejectedReleases();

  int rejectedAcquisitions();

  boolean wasAcquired();

  boolean wasReleased();

  boolean wasAcquiredAndReleased();

  boolean wasAcquireRejected();

  boolean wasReleaseRejected();

  boolean wasAcquireInvoked();

  boolean wasReleaseInvoked();
}
