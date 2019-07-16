package com.coditory.sherlock.reactor;

import com.coditory.sherlock.reactive.ReactiveSherlock;
import com.coditory.sherlock.reactive.connector.InitializationResult;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;
import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux;

final class ReactorSherlockWrapper implements ReactorSherlock {
  private final ReactiveSherlock sherlock;

  ReactorSherlockWrapper(ReactiveSherlock sherlock) {
    this.sherlock = expectNonNull(sherlock, "Expected non null sherlock");
  }

  @Override
  public Mono<InitializationResult> initialize() {
    return flowPublisherToFlux(sherlock.initialize())
        .single();
  }

  @Override
  public ReactorDistributedLock createReentrantLock(String lockId) {
    return ReactorDistributedLockWrapper.reactorLock(sherlock.createReentrantLock(lockId));
  }

  @Override
  public ReactorDistributedLock createReentrantLock(String lockId, Duration duration) {
    return ReactorDistributedLockWrapper
        .reactorLock(sherlock.createReentrantLock(lockId, duration));
  }

  @Override
  public ReactorDistributedLock createLock(String lockId) {
    return ReactorDistributedLockWrapper.reactorLock(sherlock.createLock(lockId));
  }

  @Override
  public ReactorDistributedLock createLock(String lockId, Duration duration) {
    return ReactorDistributedLockWrapper.reactorLock(sherlock.createLock(lockId, duration));
  }

  @Override
  public ReactorDistributedLock createOverridingLock(String lockId) {
    return ReactorDistributedLockWrapper.reactorLock(sherlock.createOverridingLock(lockId));
  }

  @Override
  public ReactorDistributedLock createOverridingLock(String lockId, Duration duration) {
    return ReactorDistributedLockWrapper
        .reactorLock(sherlock.createOverridingLock(lockId, duration));
  }
}
