package com.coditory.sherlock;

import java.time.Clock;

import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.util.Preconditions.expectNonNull;

/**
 * Builds {@link ReactiveSherlock} that that stores locks in memory.
 * <p>
 * Designed for testing purposes only.
 */
public final class ReactiveInMemorySherlockBuilder extends
  ReactiveSherlockWithConnectorBuilder<ReactiveInMemorySherlockBuilder> {
  private InMemoryDistributedLockStorage storage = new InMemoryDistributedLockStorage();
  private Clock clock = DEFAULT_CLOCK;

  /**
   * @return new instance of the builder
   */
  public static ReactiveInMemorySherlockBuilder reactiveInMemorySherlockBuilder() {
    return new ReactiveInMemorySherlockBuilder();
  }

  /**
   * @return new instance of in-memory sherlock with default configuration
   */
  public static ReactiveSherlock reactiveInMemorySherlock() {
    return reactiveInMemorySherlockBuilder().build();
  }

  /**
   * @param clock time provider used in locking mechanism. Default: {@link
   *   SherlockDefaults#DEFAULT_CLOCK}
   * @return the instance
   */
  public ReactiveInMemorySherlockBuilder withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  /**
   * Use shared stage for all instances of {@link ReactiveSherlock}.
   *
   * @return the instance
   */
  public ReactiveInMemorySherlockBuilder withSharedStorage() {
    this.storage = InMemoryDistributedLockStorage.singleton();
    return this;
  }

  @Override
  public ReactiveSherlock build() {
    ReactiveInMemoryDistributedLockConnector connector = new ReactiveInMemoryDistributedLockConnector(
      clock, storage);
    return super.build(connector);
  }
}
