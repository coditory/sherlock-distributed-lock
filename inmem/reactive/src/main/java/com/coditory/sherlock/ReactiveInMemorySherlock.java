package com.coditory.sherlock;

import java.time.Clock;

import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.util.Preconditions.expectNonNull;

/**
 * Builds {@link ReactiveSherlock} that stores lock in memory.
 */
public final class ReactiveInMemorySherlock extends
  ReactiveSherlockWithConnectorBuilder<ReactiveInMemorySherlock> {
  private InMemoryDistributedLockStorage storage = InMemoryDistributedLockStorage.singleton();
  private Clock clock = DEFAULT_CLOCK;

  /**
   * @return new instance of the builder
   */
  public static ReactiveInMemorySherlock builder() {
    return new ReactiveInMemorySherlock();
  }

  private ReactiveInMemorySherlock() {
    // deliberately empty
  }

  /**
   * @param clock time provider used in locking mechanism. Default: {@link
   *   SherlockDefaults#DEFAULT_CLOCK}
   * @return the instance
   */
  public ReactiveInMemorySherlock withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  public ReactiveInMemorySherlock withUnsharedStorage() {
    this.storage = new InMemoryDistributedLockStorage();
    return this;
  }

  @Override
  public ReactiveSherlock build() {
    ReactiveInMemoryDistributedLockConnector connector = new ReactiveInMemoryDistributedLockConnector(
      clock, storage);
    return super.build(connector);
  }
}
