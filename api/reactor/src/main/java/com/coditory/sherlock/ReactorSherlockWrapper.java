package com.coditory.sherlock;

import com.coditory.sherlock.connector.InitializationResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import static com.coditory.sherlock.PublisherToMonoConverter.convertToMono;
import static com.coditory.sherlock.Preconditions.expectNonNull;

final class ReactorSherlockWrapper implements ReactorSherlock {
  private final ReactiveSherlock sherlock;

  ReactorSherlockWrapper(ReactiveSherlock sherlock) {
    this.sherlock = expectNonNull(sherlock, "Expected non null sherlock");
  }

  @Override
  public Mono<InitializationResult> initialize() {
    return convertToMono(sherlock.initialize());
  }

  @Override
  public DistributedLockBuilder<ReactorDistributedLock> createLock() {
    return createLockBuilder(sherlock.createLock());
  }

  @Override
  public DistributedLockBuilder<ReactorDistributedLock> createReentrantLock() {
    return createLockBuilder(sherlock.createReentrantLock());
  }

  @Override
  public DistributedLockBuilder<ReactorDistributedLock> createOverridingLock() {
    return createLockBuilder(sherlock.createOverridingLock());
  }

  @Override
  public Mono<ReleaseResult> forceReleaseAllLocks() {
    return convertToMono(sherlock.forceReleaseAllLocks());
  }

  private DistributedLockBuilder<ReactorDistributedLock> createLockBuilder(
    DistributedLockBuilder<ReactiveDistributedLock> reactiveBuilder) {
    return reactiveBuilder.withMappedLock(ReactorDistributedLockWrapper::new);
  }
}
