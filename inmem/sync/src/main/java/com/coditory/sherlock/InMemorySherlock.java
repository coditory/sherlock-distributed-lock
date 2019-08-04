package com.coditory.sherlock;

import java.time.Clock;

import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.util.Preconditions.expectNonNull;

/**
 * Builds {@link Sherlock} that uses MongoDB for locking mechanism.
 */
public final class InMemorySherlock extends SherlockWithConnectorBuilder<InMemorySherlock> {
  private Clock clock = DEFAULT_CLOCK;
  private InMemoryDistributedLockStorage storage = new InMemoryDistributedLockStorage();

  /**
   * @return new instance of the builder
   */
  public static InMemorySherlock builder() {
    return new InMemorySherlock();
  }

  public static Sherlock inMemorySherlock() {
    return InMemorySherlock.builder().build();
  }

  InMemorySherlock() {
    // deliberately empty
  }

  /**
   * @param clock time provider used in locking mechanism. Default: {@link
   *   SherlockDefaults#DEFAULT_CLOCK}
   * @return the instance
   */
  public InMemorySherlock withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  public InMemorySherlock withSharedStorage() {
    this.storage = InMemoryDistributedLockStorage.singleton();
    return this;
  }

  /**
   * @return sherlock instance
   * @throws IllegalArgumentException when some required values are missing
   */
  @Override
  public Sherlock build() {
    InMemoryDistributedLockConnector connector = new InMemoryDistributedLockConnector(
      clock, storage);
    return super.build(connector);
  }
}
