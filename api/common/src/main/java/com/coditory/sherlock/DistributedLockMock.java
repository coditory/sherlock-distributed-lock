package com.coditory.sherlock;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.UuidGenerator.uuid;

public final class DistributedLockMock implements DistributedLock {
  private final InMemoryDistributedLock lock;
  // mock counters
  private int releases = 0;
  private int acquisitions = 0;
  private int successfulReleases = 0;
  private int successfulAcquisitions = 0;

  private DistributedLockMock(InMemoryDistributedLock lock) {
    this.lock = expectNonNull(lock);
  }

  @Override
  public String getId() {
    return lock.getId();
  }

  @Override
  public boolean acquire() {
    return acquire(lock::acquire);
  }

  @Override
  public boolean acquire(Duration duration) {
    return acquire(() -> lock.acquire(duration));
  }

  @Override
  public boolean acquireForever() {
    return acquire(lock::acquireForever);
  }

  synchronized private boolean acquire(Supplier<Boolean> acquireAction) {
    acquisitions++;
    boolean result = acquireAction.get();
    if (result) {
      successfulAcquisitions++;
    }
    return result;
  }

  @Override
  synchronized public boolean release() {
    releases++;
    boolean result = lock.release();
    if (result) {
      successfulReleases++;
    }
    return result;
  }

  @Override
  public LockState getState() {
    return lock.state;
  }

  public void markAsLocked(Instant expiresAt) {
    this.markAsLocked(expiresAt);
  }

  public void markAsLocked(Duration duration) {
    this.markAsLocked(duration);
  }

  public void markAsLocked() {
    this.markAsLocked();
  }

  public void markAsAcquired(Instant expiresAt) {
    this.markAsAcquired(expiresAt);
  }

  public void markAsAcquired(Duration duration) {
    this.markAsAcquired(duration);
  }

  public void markAsAcquired() {
    this.markAsAcquired();
  }

  public void markAsUnlocked() {
    this.markAsUnlocked();
  }

  /**
   * @return the count of successful releases
   */
  synchronized public int successfulReleases() {
    return successfulReleases;
  }

  /**
   * @return the count of successful acquisitions
   */
  synchronized public int successfulAcquisitions() {
    return successfulAcquisitions;
  }

  /**
   * @return the count of all releases (successful and unsuccessful)
   */
  synchronized public int releases() {
    return releases;
  }

  /**
   * @return the count of all acquisitions (successful and unsuccessful)
   */
  synchronized public int acquisitions() {
    return acquisitions;
  }

  /**
   * @return the count of rejected releases
   */
  synchronized public int rejectedReleases() {
    return releases() - successfulReleases();
  }

  /**
   * @return the count of rejected acquisitions
   */
  public int rejectedAcquisitions() {
    return acquisitions() - successfulAcquisitions();
  }

  /**
   * @return true if lock was successfully acquired at least once
   */
  synchronized public boolean wasAcquired() {
    return successfulAcquisitions() > 0;
  }

  /**
   * @return true if lock was successfully released at least once
   */
  synchronized public boolean wasReleased() {
    return successfulReleases() > 0;
  }

  /**
   * @return true if lock was successfully acquired and released
   */
  synchronized public boolean wasAcquiredAndReleased() {
    return wasAcquired() && wasReleased();
  }

  /**
   * @return true if lock was acquired without success at least once
   */
  synchronized public boolean wasAcquireRejected() {
    return successfulAcquisitions() < acquisitions();
  }

  /**
   * @return true if lock was released without success at least once
   */
  synchronized public boolean wasReleaseRejected() {
    return successfulReleases() < releases();
  }

  /**
   * @return true if acquire operation was invoked at least once
   */
  synchronized public boolean wasAcquireInvoked() {
    return acquisitions() > 0;
  }

  /**
   * @return true if release operation was invoked at least once
   */
  synchronized public boolean wasReleaseInvoked() {
    return releases() > 0;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private LockId lockId = LockId.of(uuid());
    private boolean reentrant = false;
    private LockState state = LockState.UNLOCKED;
    private LockDuration lockDuration = DEFAULT_LOCK_DURATION;
    private Instant expiresAt = null;
    private Clock clock = Clock.systemUTC();

    private Builder() {
      // deliberately empty
    }

    public Builder lockId(LockId lockId) {
      this.lockId = lockId;
      return this;
    }

    public Builder clock(Clock clock) {
      this.clock = clock;
      return this;
    }

    public Builder reentrant(boolean reentrant) {
      this.reentrant = reentrant;
      return this;
    }

    public Builder defaultLockDuration(Duration lockDuration) {
      this.lockDuration = LockDuration.of(lockDuration);
      return this;
    }

    public Builder state(LockState state) {
      this.state = state;
      return this;
    }

    public Builder expiresAt(Instant expiresAt) {
      this.expiresAt = expiresAt;
      return this;
    }

    public InMemoryDistributedLock build() {
      return new InMemoryDistributedLock(lockId, state, reentrant, clock, lockDuration, expiresAt);
    }
  }

  private static final class InMemoryDistributedLock implements DistributedLock {
    private final LockId lockId;
    private final boolean reentrant;
    private final Clock clock;
    private final LockDuration defaultLockDuration;
    private LockState state;
    private Instant expiresAt;

    private InMemoryDistributedLock(
        LockId lockId,
        LockState state,
        boolean reentrant,
        Clock clock,
        LockDuration defaultLockDuration,
        Instant expiresAt) {
      this.lockId = expectNonNull(lockId);
      this.state = expectNonNull(state);
      this.clock = expectNonNull(clock);
      this.defaultLockDuration = expectNonNull(defaultLockDuration);
      this.reentrant = reentrant;
      this.expiresAt = expiresAt;
    }

    @Override
    public String getId() {
      return lockId.getValue();
    }

    @Override
    public boolean acquire() {
      return acquireLock(defaultLockDuration);
    }

    @Override
    public boolean acquire(Duration duration) {
      return acquireLock(LockDuration.of(duration));
    }

    @Override
    public boolean acquireForever() {
      return acquireLock(null);
    }

    synchronized private boolean acquireLock(LockDuration duration) {
      updateState();
      if (LockState.UNLOCKED.equals(state)) {
        this.state = LockState.ACQUIRED;
        return true;
      }
      if (LockState.ACQUIRED.equals(state) && reentrant) {
        expiresAt = duration != null
            ? clock.instant().plus(duration.getValue())
            : null;
        return true;
      }
      return false;
    }

    @Override
    synchronized public boolean release() {
      updateState();
      if (LockState.ACQUIRED.equals(state)) {
        this.state = LockState.UNLOCKED;
        return true;
      }
      return false;
    }

    @Override
    synchronized public LockState getState() {
      return state;
    }

    synchronized public void markAsLocked(Instant expiresAt) {
      this.expiresAt = expiresAt;
      this.state = LockState.LOCKED;
    }

    synchronized public void markAsLocked(Duration duration) {
      markAsLocked(clock.instant().plus(duration));
    }

    synchronized public void markAsLocked() {
      this.expiresAt = null;
      this.state = LockState.LOCKED;
    }

    synchronized public void markAsAcquired(Instant expiresAt) {
      this.expiresAt = expiresAt;
      this.state = LockState.ACQUIRED;
    }

    synchronized public void markAsAcquired(Duration duration) {
      markAsLocked(clock.instant().plus(duration));
    }

    synchronized public void markAsAcquired() {
      this.expiresAt = null;
      this.state = LockState.ACQUIRED;
    }

    synchronized public void markAsUnlocked() {
      this.state = LockState.UNLOCKED;
    }

    private void updateState() {
      if (expiresAt != null && clock.instant().isAfter(expiresAt)) {
        state = LockState.UNLOCKED;
      }
    }
  }
}
